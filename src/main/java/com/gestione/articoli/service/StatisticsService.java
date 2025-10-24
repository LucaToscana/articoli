package com.gestione.articoli.service;

import com.gestione.articoli.dto.OrdineRisultatoDto;
import com.gestione.articoli.dto.StatisticsDto;
import com.gestione.articoli.model.Articolo;
import com.gestione.articoli.model.Ordine;
import com.gestione.articoli.model.OrdineArticolo;
import com.gestione.articoli.model.OrdineRisultato;
import com.gestione.articoli.repository.OrdineRisultatoRepository;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final OrdineRisultatoRepository ordineRisultatoRepository;

    @Transactional(readOnly = true)
    public StatisticsDto calculateStatistics(LocalDateTime start, LocalDateTime end, Long aziendaId) {
        List<OrdineRisultato> risultati = ordineRisultatoRepository.findOrdiniByDataRangeAndAzienda(start, end, aziendaId);

        if (risultati.isEmpty()) {
            return buildEmptyDto(start, end);
        }

        Accumulator acc = accumulateBaseData(risultati);
        KPI kpi = calculateKPI(acc, risultati.size());

        return buildStatisticsDto(start, end, acc, kpi, risultati.size());
    }

    // ==============================
    // Sottometodi
    // ==============================

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
        
        // Totali per tipo di lavorazione
        BigDecimal molatura = BigDecimal.ZERO;
        BigDecimal lucidatura = BigDecimal.ZERO;
        BigDecimal saldatura = BigDecimal.ZERO;
        BigDecimal foratura = BigDecimal.ZERO;
        BigDecimal filettatura = BigDecimal.ZERO;
        BigDecimal montaggio = BigDecimal.ZERO;
        BigDecimal scatolatura = BigDecimal.ZERO;
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

    private Accumulator accumulateBaseData(List<OrdineRisultato> risultati) {
        Accumulator acc = new Accumulator();

        for (OrdineRisultato r : risultati) {
            Ordine ordine = r.getOrdine();
            OrdineArticolo ordineArticolo = ordine.getArticoli().stream()
                    .filter(oa -> oa.getArticolo().getId().equals(r.getArticolo().getId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Articolo non trovato nell’ordine"));

            BigDecimal minuti = r.getMolaturaFatturabile()
                    .add(r.getLucidaturaFatturabile())
                    .add(r.getSaldaturaFatturabile())
                    .add(r.getForaturaFatturabile())
                    .add(r.getFilettaturaFatturabile())
                    .add(r.getMontaggioFatturabile())
                    .add(r.getScatolaturaFatturabile());

            acc.totaleMinuti = acc.totaleMinuti.add(minuti);
            acc.totaleOre = acc.totaleOre.add(minuti.divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP));

            acc.molatura = acc.molatura.add(r.getMolaturaFatturabile());
            acc.lucidatura = acc.lucidatura.add(r.getLucidaturaFatturabile());
            acc.saldatura = acc.saldatura.add(r.getSaldaturaFatturabile());
            acc.foratura = acc.foratura.add(r.getForaturaFatturabile());
            acc.filettatura = acc.filettatura.add(r.getFilettaturaFatturabile());
            acc.montaggio = acc.montaggio.add(r.getMontaggioFatturabile());
            acc.scatolatura = acc.scatolatura.add(r.getScatolaturaFatturabile());

            BigDecimal ricavoNetto = ordineArticolo.getPrezzo().multiply(r.getQuantita());
            acc.totaleRicavoNetto = acc.totaleRicavoNetto.add(ricavoNetto);

            BigDecimal ivaPercent = r.getOrdine().getIva() != null ? r.getOrdine().getIva() : r.getIVA_STANDARD();
            BigDecimal ivaImporto = ricavoNetto.multiply(ivaPercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            acc.totaleIva = acc.totaleIva.add(ivaImporto);
            acc.totaleRicavoLordo = acc.totaleRicavoLordo.add(ricavoNetto.add(ivaImporto));

         // Converto i minuti in ore per calcolare correttamente i costi
            BigDecimal ore = minuti.divide(BigDecimal.valueOf(60), 4, RoundingMode.HALF_UP);

            // Calcolo costi proporzionati alle ore lavorate
            BigDecimal costoPersEffettivo = r.getCOSTO_PERSONALE_ORARIO_MEDIO().multiply(ore);
            BigDecimal costoFisEffettivo = r.getCOSTO_ORARIO_FISSO().multiply(ore);

            // Aggiorno l'accumulatore
            acc.costoPersonale = acc.costoPersonale.add(costoPersEffettivo);
            acc.costoFisso = acc.costoFisso.add(costoFisEffettivo);
            acc.totaleCosti = acc.totaleCosti.add(costoPersEffettivo).add(costoFisEffettivo);

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
                .build();
    }

    private BigDecimal safeDivide(BigDecimal numerator, BigDecimal denominator) {
        if (denominator == null || denominator.compareTo(BigDecimal.ZERO) == 0)
            return BigDecimal.ZERO;
        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }

    @Transactional
    public Map<Long, List<OrdineRisultatoDto>> getOrdiniDettaglio(LocalDateTime start, LocalDateTime end, Long aziendaId) {
        start = start.with(LocalTime.MIN);
        LocalDateTime todayEnd = ZonedDateTime.now(ZoneId.of("Europe/Rome")).toLocalDateTime().with(LocalTime.MAX);

        if (end.isAfter(todayEnd)) {
            end = todayEnd;
        } else {
            end = end.with(LocalTime.MAX);
        }

        if (start.isAfter(end)) {
            start = end.minusDays(1).with(LocalTime.MIN);
        }

        List<OrdineRisultato> risultati = ordineRisultatoRepository.findOrdiniByDataRangeAndAzienda(start, end, aziendaId);

        List<OrdineRisultatoDto> dtos = risultati.stream().map(r -> {
            OrdineRisultatoDto dto = new OrdineRisultatoDto();
            Ordine o = r.getOrdine();
            OrdineArticolo ordineArticoloSpecifico = o.getArticoli().stream()
                    .filter(oa -> oa.getArticolo().getId().equals(r.getArticolo().getId()))
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
            dto.setPrezzo(ordineArticoloSpecifico.getPrezzo());
            dto.setQuantita(r.getQuantita());

            dto.setOrdineId(o.getId());
            dto.setWorkStatus(o.getWorkStatus());
            dto.setDataOrdine(o.getDataOrdine());
            dto.setAziendaNome(o.getAzienda() != null ? o.getAzienda().getNome() : null);

            Articolo a = r.getArticolo();
            dto.setArticoloId(a.getId());
            String codice = a.getCodice();
            if (a.getCodiceComponente() != null && !a.getCodiceComponente().isEmpty()) {
                codice += "/" + a.getCodiceComponente();
            }
            dto.setArticoloCodice(codice);

            return dto;
        }).toList();

        return dtos.stream().collect(Collectors.groupingBy(OrdineRisultatoDto::getOrdineId, LinkedHashMap::new, Collectors.toList()));
    }
}
