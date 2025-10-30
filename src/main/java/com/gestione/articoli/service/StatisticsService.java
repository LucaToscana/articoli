package com.gestione.articoli.service;

import com.gestione.articoli.dto.OrdineConRisultatiDto;
import com.gestione.articoli.dto.OrdineDto;
import com.gestione.articoli.dto.OrdineRisultatoDto;
import com.gestione.articoli.dto.StatisticsDto;
import com.gestione.articoli.model.Ordine;
import com.gestione.articoli.model.OrdineRisultato;
import com.gestione.articoli.model.OrdineArticolo;
import com.gestione.articoli.repository.OrdineRisultatoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final OrdineRisultatoRepository ordineRisultatoRepository;
    private final OrdineService ordineService;

    // ==============================
    // 🔹 Statistiche per intervallo di date e azienda
    // ==============================
    @Transactional(readOnly = true)
    public StatisticsDto calculateStatistics(LocalDateTime start, LocalDateTime end, Long aziendaId) {
        List<OrdineRisultato> risultati = ordineRisultatoRepository.findOrdiniByDataRangeAndAzienda(start, end, aziendaId);
        if (risultati.isEmpty()) return buildEmptyDto(start, end);

        Accumulator acc = accumulateBaseData(risultati);
        KPI kpi = calculateKPI(acc, risultati.stream().map(r -> r.getOrdine().getId()).collect(Collectors.toSet()).size());
        return buildStatisticsDto(start, end, acc, kpi, risultati.size());
    }

    // ==============================
    // 🔹 Statistiche per azienda (tutti i tempi)
    // ==============================
    @Transactional(readOnly = true)
    public StatisticsDto calculateStatisticsByAzienda(Long aziendaId) {
        List<OrdineRisultato> risultati = ordineRisultatoRepository.findByAziendaId(aziendaId);
        if (risultati.isEmpty()) return buildEmptyDto(LocalDateTime.now().minusYears(1), LocalDateTime.now());

        Accumulator acc = accumulateBaseData(risultati);
        Set<Long> ordiniDistinct = risultati.stream().map(r -> r.getOrdine().getId()).collect(Collectors.toSet());
        KPI kpi = calculateKPI(acc, ordiniDistinct.size());

        LocalDateTime minDate = risultati.stream().map(OrdineRisultato::getDataRisultato)
                                         .min(LocalDateTime::compareTo).orElse(LocalDateTime.now());
        LocalDateTime maxDate = risultati.stream().map(OrdineRisultato::getDataRisultato)
                                         .max(LocalDateTime::compareTo).orElse(LocalDateTime.now());

        return buildStatisticsDto(minDate, maxDate, acc, kpi, ordiniDistinct.size());
    }

    // ==============================
    // 🔹 Statistiche per singolo ordine
    // ==============================
    @Transactional(readOnly = true)
    public StatisticsDto calculateStatisticsByOrdine(Long ordineId) {
        List<OrdineRisultato> risultati = ordineRisultatoRepository.findByOrdineId(ordineId);
        if (risultati.isEmpty()) return buildEmptyDto(LocalDateTime.now(), LocalDateTime.now());

        Accumulator acc = accumulateBaseData(risultati);
        KPI kpi = calculateKPI(acc, 1);

        LocalDateTime start = risultati.get(0).getOrdine().getDataOrdine();
        LocalDateTime end = risultati.stream()
                                     .map(OrdineRisultato::getDataRisultato)
                                     .max(LocalDateTime::compareTo).orElse(start);

        return buildStatisticsDto(start, end, acc, kpi, 1);
    }

    // ==============================
    // 🔹 Dettaglio ordini
    // ==============================
    @Transactional(readOnly = true)
    public Map<Long, OrdineConRisultatiDto> getOrdiniDettaglio(LocalDateTime start, LocalDateTime end, Long aziendaId) {
        start = start.with(LocalTime.MIN);
        LocalDateTime todayEnd = ZonedDateTime.now(ZoneId.of("Europe/Rome")).toLocalDateTime().with(LocalTime.MAX);
        end = end.isAfter(todayEnd) ? todayEnd : end.with(LocalTime.MAX);
        if (start.isAfter(end)) start = end.minusDays(1).with(LocalTime.MIN);

        List<OrdineRisultato> risultati = ordineRisultatoRepository.findOrdiniByDataRangeAndAzienda(start, end, aziendaId);
        List<OrdineRisultatoDto> dtos = risultati.stream().map(this::mapToDto).toList();

        List<Long> ordineIds = dtos.stream().map(OrdineRisultatoDto::getOrdineId).distinct().toList();
        List<OrdineDto> ordini = ordineService.findAllOrderByIds(ordineIds);

        Map<Long, OrdineDto> ordineMap = ordini.stream().collect(Collectors.toMap(OrdineDto::getId, o -> o, (a,b)->a, LinkedHashMap::new));
        Map<Long, List<OrdineRisultatoDto>> risultatiPerOrdine = dtos.stream().collect(Collectors.groupingBy(OrdineRisultatoDto::getOrdineId, LinkedHashMap::new, Collectors.toList()));

        Map<Long, OrdineConRisultatiDto> finalMap = new LinkedHashMap<>();
        risultatiPerOrdine.forEach((id, rList) -> finalMap.put(id, new OrdineConRisultatiDto(ordineMap.get(id), rList)));

        return finalMap;
    }

    // ==============================
    // 🔹 Mappatura OrdineRisultato -> DTO
    // ==============================
    private OrdineRisultatoDto mapToDto(OrdineRisultato r) {
        OrdineRisultatoDto dto = new OrdineRisultatoDto();
        var ordine = r.getOrdine();
        var articolo = r.getArticolo();
        var ordineArticolo = ordine.getArticoli().stream()
                .filter(oa -> oa.getArticolo().getId().equals(articolo.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Articolo non trovato nell’ordine"));

        dto.setId(r.getId());
        dto.setMolaturaReale(r.getMolaturaReale());
        dto.setMolaturaFatturabile(r.getMolaturaFatturabile());
        dto.setLucidaturaReale(r.getLucidaturaReale());
        dto.setLucidaturaFatturabile(r.getLucidaturaFatturabile());
        dto.setSaldaturaReale(r.getSaldaturaReale());
        dto.setSaldaturaFatturabile(r.getSaldaturaFatturabile());
        dto.setForaturaReale(r.getForaturaReale());
        dto.setForaturaFatturabile(r.getForaturaFatturabile());
        dto.setFilettaturaReale(r.getFilettaturaReale());
        dto.setFilettaturaFatturabile(r.getFilettaturaFatturabile());
        dto.setMontaggioReale(r.getMontaggioReale());
        dto.setMontaggioFatturabile(r.getMontaggioFatturabile());
        dto.setScatolaturaReale(r.getScatolaturaReale());
        dto.setScatolaturaFatturabile(r.getScatolaturaFatturabile());
        dto.setDataRisultato(r.getDataRisultato());
        dto.setPrezzo(ordineArticolo.getPrezzo());
        dto.setQuantita(r.getQuantita());
        dto.setOrdineId(ordine.getId());
        dto.setWorkStatus(ordine.getWorkStatus());
        dto.setDataOrdine(ordine.getDataOrdine());
        dto.setStaccareReale(r.getStaccareReale());
        dto.setStaccareFatturabile(r.getStaccareFatturabile());
        dto.setLavareReale(r.getLavareReale());
        dto.setLavareFatturabile(r.getLavareFatturabile());
        dto.setAnnerireReale(r.getAnnerireReale());
        dto.setAnnerireFatturabile(r.getAnnerireFatturabile());
        dto.setSatinareReale(r.getSatinareReale());
        dto.setSatinareFatturabile(r.getSatinareFatturabile());
        dto.setAziendaNome(ordine.getAzienda() != null ? ordine.getAzienda().getNome() : null);

        String codice = articolo.getCodice();
        if (articolo.getCodiceComponente() != null && !articolo.getCodiceComponente().isEmpty()) {
            codice += "/" + articolo.getCodiceComponente();
        }
        dto.setArticoloCodice(codice);
        dto.setArticoloId(articolo.getId());

        dto.setPrezzoOrarioFisso(r.getPREZZO_ORARIO_FISSO());
        dto.setPrezzoEffettivo(r.getPREZZO_EFFETTIVO());
        dto.setCostoOrarioFisso(r.getCOSTO_ORARIO_FISSO());
        dto.setCostoPersonaleOrarioMedio(r.getCOSTO_PERSONALE_ORARIO_MEDIO());
        dto.setIvaStandard(r.getIVA_STANDARD());
        dto.setRicaricoBase(r.getRICARICO_BASE());

        return dto;
    }

    // ==============================
    // 🔹 Helper: Accumulator & KPI
    // ==============================
    private static class Accumulator {
        BigDecimal totaleOre = BigDecimal.ZERO;
        BigDecimal totaleMinuti = BigDecimal.ZERO;
        BigDecimal totaleArticoli = BigDecimal.ZERO;
        BigDecimal totaleRicavoNetto = BigDecimal.ZERO;
        BigDecimal totaleRicavoLordo = BigDecimal.ZERO;
        BigDecimal totaleIva = BigDecimal.ZERO;
        BigDecimal totaleCosti = BigDecimal.ZERO;
        BigDecimal costoPersonale = BigDecimal.ZERO;
        BigDecimal costoFisso = BigDecimal.ZERO;

        // lavorazioni esistenti
        BigDecimal molatura = BigDecimal.ZERO;
        BigDecimal lucidatura = BigDecimal.ZERO;
        BigDecimal saldatura = BigDecimal.ZERO;
        BigDecimal foratura = BigDecimal.ZERO;
        BigDecimal filettatura = BigDecimal.ZERO;
        BigDecimal montaggio = BigDecimal.ZERO;
        BigDecimal scatolatura = BigDecimal.ZERO;

        // nuove lavorazioni
        BigDecimal staccare = BigDecimal.ZERO;
        BigDecimal lavare = BigDecimal.ZERO;
        BigDecimal annerire = BigDecimal.ZERO;
        BigDecimal satinare = BigDecimal.ZERO;
    }


    private static class KPI {
        BigDecimal utileNetto;
        BigDecimal produttivitaOraria;
        BigDecimal marginePercentuale;
        BigDecimal valoreMedioOrdine;
        BigDecimal valoreMedioArticolo;
        BigDecimal mediaArticoliOra;
        BigDecimal utileMedioArticolo;
        BigDecimal utileMedioOrdine;
        BigDecimal iva10;
        BigDecimal iva22;
    }

    // ==============================
    // 🔹 Accumulazione base dati
    // ==============================
    private Accumulator accumulateBaseData(List<OrdineRisultato> risultati) {
        Accumulator acc = new Accumulator();
        for (OrdineRisultato r : risultati) {
            Ordine ordine = r.getOrdine();
            OrdineArticolo oa = ordine.getArticoli().stream()
                    .filter(a -> a.getArticolo().getId().equals(r.getArticolo().getId()))
                    .findFirst()
                    .orElseThrow();

            BigDecimal minuti = r.getMolaturaFatturabile()
                    .add(r.getLucidaturaFatturabile())
                    .add(r.getSaldaturaFatturabile())
                    .add(r.getForaturaFatturabile())
                    .add(r.getFilettaturaFatturabile())
                    .add(r.getMontaggioFatturabile())
                    .add(r.getScatolaturaFatturabile())
                    .add(r.getStaccareFatturabile())
                    .add(r.getLavareFatturabile())
                    .add(r.getAnnerireFatturabile())
                    .add(r.getSatinareFatturabile());

            acc.totaleMinuti = acc.totaleMinuti.add(minuti);
            acc.totaleOre = acc.totaleOre.add(minuti.divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP));

            acc.molatura = acc.molatura.add(r.getMolaturaFatturabile());
            acc.lucidatura = acc.lucidatura.add(r.getLucidaturaFatturabile());
            acc.saldatura = acc.saldatura.add(r.getSaldaturaFatturabile());
            acc.foratura = acc.foratura.add(r.getForaturaFatturabile());
            acc.filettatura = acc.filettatura.add(r.getFilettaturaFatturabile());
            acc.montaggio = acc.montaggio.add(r.getMontaggioFatturabile());
            acc.scatolatura = acc.scatolatura.add(r.getScatolaturaFatturabile());
            acc.staccare = acc.staccare.add(r.getStaccareFatturabile());
            acc.lavare = acc.lavare.add(r.getLavareFatturabile());
            acc.annerire = acc.annerire.add(r.getAnnerireFatturabile());
            acc.satinare = acc.satinare.add(r.getSatinareFatturabile());
            
            BigDecimal ricavoNetto = oa.getPrezzo().multiply(r.getQuantita());
            acc.totaleRicavoNetto = acc.totaleRicavoNetto.add(ricavoNetto);

            BigDecimal ivaPercent = ordine.getIva() != null ? ordine.getIva() : r.getIVA_STANDARD();
            BigDecimal ivaImporto = ricavoNetto.multiply(ivaPercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            acc.totaleIva = acc.totaleIva.add(ivaImporto);
            acc.totaleRicavoLordo = acc.totaleRicavoLordo.add(ricavoNetto.add(ivaImporto));

            BigDecimal ore = minuti.divide(BigDecimal.valueOf(60), 4, RoundingMode.HALF_UP);
            BigDecimal costoPersEff = r.getCOSTO_PERSONALE_ORARIO_MEDIO().multiply(ore);
            BigDecimal costoFisEff = r.getCOSTO_ORARIO_FISSO().multiply(ore);

            acc.costoPersonale = acc.costoPersonale.add(costoPersEff);
            acc.costoFisso = acc.costoFisso.add(costoFisEff);
            acc.totaleCosti = acc.totaleCosti.add(costoPersEff).add(costoFisEff);

            acc.totaleArticoli = acc.totaleArticoli.add(r.getQuantita());
        }
        return acc;
    }

    private KPI calculateKPI(Accumulator acc, int totaleOrdini) {
        KPI kpi = new KPI();
        kpi.utileNetto = acc.totaleRicavoNetto.subtract(acc.totaleCosti);
        kpi.produttivitaOraria = safeDivide(acc.totaleRicavoNetto, acc.totaleOre);
        kpi.marginePercentuale = safeDivide(kpi.utileNetto.multiply(BigDecimal.valueOf(100)), acc.totaleRicavoNetto);
        kpi.valoreMedioOrdine = safeDivide(acc.totaleRicavoNetto, BigDecimal.valueOf(totaleOrdini));
        kpi.valoreMedioArticolo = safeDivide(acc.totaleRicavoNetto, acc.totaleArticoli);
        kpi.mediaArticoliOra = safeDivide(acc.totaleArticoli, acc.totaleOre);
        kpi.utileMedioArticolo = safeDivide(kpi.utileNetto, acc.totaleArticoli);
        kpi.utileMedioOrdine = safeDivide(kpi.utileNetto, BigDecimal.valueOf(totaleOrdini));
        kpi.iva10 = acc.totaleRicavoNetto.multiply(BigDecimal.valueOf(0.10));
        kpi.iva22 = acc.totaleRicavoNetto.multiply(BigDecimal.valueOf(0.22));
        return kpi;
    }

    private BigDecimal safeDivide(BigDecimal numerator, BigDecimal denominator) {
        if (denominator == null || denominator.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }

    private StatisticsDto buildEmptyDto(LocalDateTime start, LocalDateTime end) {
        return StatisticsDto.builder()
                .dataInizio(start.toLocalDate())
                .dataFine(end.toLocalDate())
                .totaleOrdini(0L)
                .totaleArticoliVenduti(0L)
                .totaleRicavoNetto(BigDecimal.ZERO)
                .totaleRicavoLordo(BigDecimal.ZERO)
                .totaleCosti(BigDecimal.ZERO)
                .utileNetto(BigDecimal.ZERO)
                .costoPersonale(BigDecimal.ZERO)
                .costoFisso(BigDecimal.ZERO)
                .totaleOre(BigDecimal.ZERO)
                .totaleMinuti(BigDecimal.ZERO)
                .produttivitaOraria(BigDecimal.ZERO)
                .valoreMedioOrdine(BigDecimal.ZERO)
                .valoreMedioArticolo(BigDecimal.ZERO)
                .mediaArticoliOra(BigDecimal.ZERO)
                .utileMedioArticolo(BigDecimal.ZERO)
                .utileMedioOrdine(BigDecimal.ZERO)
                .iva10Percent(BigDecimal.ZERO)
                .iva22Percent(BigDecimal.ZERO)
                .build();
    }

    private StatisticsDto buildStatisticsDto(LocalDateTime start, LocalDateTime end, Accumulator acc, KPI kpi, int totaleOrdini) {
        return StatisticsDto.builder()
                .dataInizio(start.toLocalDate())
                .dataFine(end.toLocalDate())
                .totaleOrdini(totaleOrdini)
                .totaleArticoliVenduti(acc.totaleArticoli.longValue())
                .totaleOre(acc.totaleOre)
                .totaleMinuti(acc.totaleMinuti)
                .totaleRicavoNetto(acc.totaleRicavoNetto)
                .totaleRicavoLordo(acc.totaleRicavoLordo)
                .totaleIva(acc.totaleIva)
                .totaleCosti(acc.totaleCosti)
                .costoPersonale(acc.costoPersonale)
                .costoFisso(acc.costoFisso)
                .utileNetto(kpi.utileNetto)
                .produttivitaOraria(kpi.produttivitaOraria)
                .marginePercentuale(kpi.marginePercentuale)
                .valoreMedioOrdine(kpi.valoreMedioOrdine)
                .valoreMedioArticolo(kpi.valoreMedioArticolo)
                .mediaArticoliOra(kpi.mediaArticoliOra)
                .utileMedioArticolo(kpi.utileMedioArticolo)
                .utileMedioOrdine(kpi.utileMedioOrdine)
                .iva10Percent(kpi.iva10)
                .iva22Percent(kpi.iva22)
                .totaleMolatura(acc.molatura)
                .totaleLucidatura(acc.lucidatura)
                .totaleSaldatura(acc.saldatura)
                .totaleForatura(acc.foratura)
                .totaleFilettatura(acc.filettatura)
                .totaleMontaggio(acc.montaggio)
                .totaleScatolatura(acc.scatolatura)
                .totaleStaccare(acc.staccare)
                .totaleLavare(acc.lavare)
                .totaleAnnerire(acc.annerire)
                .totaleSatinare(acc.satinare)
                .build();
    }
}
