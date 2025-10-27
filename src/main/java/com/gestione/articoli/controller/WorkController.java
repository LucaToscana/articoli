package com.gestione.articoli.controller;

import com.gestione.articoli.dto.StartWorkDto;
import com.gestione.articoli.dto.TotalWorkTimeDto;
import com.gestione.articoli.dto.UserDto;
import com.gestione.articoli.dto.WorkDto;
import com.gestione.articoli.model.WorkStatus;
import com.gestione.articoli.service.UserService;
import com.gestione.articoli.service.WorkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/works")
@RequiredArgsConstructor
public class WorkController {

    private final WorkService workService;
    private final UserService userService;

    /* ========= CREATE ========= */

    /**
     * Crea un nuovo Work.
     *
     * @param dto WorkDto contenente i dati del nuovo Work
     * @return WorkDto creato
     */
    @PostMapping
    public ResponseEntity<WorkDto> createWork(@RequestBody WorkDto dto) {
        return ResponseEntity.ok(workService.createWork(dto));
    }

    /**
     * Avvia una lavorazione per un operatore.
     *
     * @param dto StartWorkDto contenente i dati della lavorazione
     * @return messaggio di conferma
     */
    @PostMapping("/start")
    public ResponseEntity<String> startWork(@RequestBody StartWorkDto dto) {
        workService.startWork(dto);
        return ResponseEntity.ok("Lavorazione avviata");
    }

    /* ========= READ ========= */

    /**
     * Recupera tutti i Work.
     */
    @GetMapping
    public ResponseEntity<List<WorkDto>> getAllWorks() {
        return ResponseEntity.ok(workService.getAllWorks());
    }

    /**
     * Recupera un Work per ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<WorkDto> getWorkById(@PathVariable Long id) {
        return ResponseEntity.ok(workService.getWorkById(id));
    }

    /**
     * Recupera i Work per ID dell'ordine.
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<WorkDto>> getWorksByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(workService.getWorksByOrderId(orderId));
    }

    /**
     * Recupera i Work per ID dell'ordine-articolo.
     */
    @GetMapping("/order-article/{orderArticleId}")
    public ResponseEntity<List<WorkDto>> getWorksByOrderArticle(@PathVariable Long orderArticleId) {
        return ResponseEntity.ok(workService.getWorksByOrderArticle(orderArticleId));
    }

    /**
     * Recupera il tempo totale di lavorazione per ordine-articolo.
     */
    @GetMapping("/order-article/{orderArticleId}/total-time-dto")
    public ResponseEntity<TotalWorkTimeDto> getTotalWorkTimeDto(@PathVariable Long orderArticleId) {
        return ResponseEntity.ok(workService.getTotalWorkTimeDto(orderArticleId));
    }

    /**
     * Recupera i Work di un ordine-articolo filtrati per orderworks.
     */
    @GetMapping("/orderworks")
    public ResponseEntity<List<WorkDto>> getWorksByOrderArticleOrderworks(@RequestParam Long orderArticleId) {
        return ResponseEntity.ok(workService.getWorksByOrderArticleId(orderArticleId));
    }

    /**
     * Recupera i Work di disponibilità in corso.
     */
    @GetMapping("/availability/in-progress")
    public List<WorkDto> getInProgressAvailabilityWorks() {
        return workService.getInProgressAvailabilityWorks();
    }

    /**
     * Recupera i Work di disponibilità in corso per ordine specifico.
     */
    @GetMapping("/availability/{orderId}")
    public List<WorkDto> getInProgressAvailabilityWorksByOrderId(@PathVariable Long orderId) {
        return workService.getAvailabilityWorksByOrder(orderId);
    }

    /**
     * Recupera tutti i Work lotto.
     */
    @GetMapping("/lotto")
    public List<WorkDto> getLottoWorks() {
        return workService.getLottoWorks();
    }

    /**
     * Recupera i Work lotto in corso.
     */
    @GetMapping("/lotto/in-progress")
    public List<WorkDto> getInProgressLottoWorks() {
        return workService.getInProgressLottoWorks();
    }
    /**
     * Recupera i Work lotto in corso.
     */
    @GetMapping("/lotto/in-progress/order/in-progress")
    public List<WorkDto> getInProgressLottoWorksWithOrderInProgress() {
        return workService.getInProgressLottoWorksWithOrderInProgress();
    }
    /**
     * Recupera i Work lotto per ordine specifico.
     */
    @GetMapping("/lotto/{id}")
    public ResponseEntity<List<WorkDto>> getLottoWorksByOrder(@PathVariable Long id) {
        return ResponseEntity.ok(workService.getLottoWorksByOrder(id));
    }

    /**
     * Recupera i Work manuali in corso.
     */
    @GetMapping("/manual/in-progress")
    public List<WorkDto> getInProgressManualWorks() {
        return workService.getInProgressManualWorks();
    }
    /**
     * Recupera i Work manuali in corso creati dall'admin (creator_id = 1).
     */
    @GetMapping("/manual/in-progress/admin")
    public List<WorkDto> getInProgressManualWorksCreatedByAdmin() {
        return workService.getInProgressManualWorksCreatedByAdmin();
    }
    /**
     * Recupera i Work manuali in corso di un ordine.
     */
    @GetMapping("/manual/in-progress/{orderId}")
    public List<WorkDto> getInProgressManualWorks(@PathVariable Long orderId){
        return workService.getInProgressManualByOrder(orderId);
    }
    
    
    /**
     * Recupera i lavori manuali attivi per un ordine specifico IN CORSO,
     * includendo il totale dei minuti di esecuzione per ogni gruppo.
     *
     * @param orderId ID dell'ordine
     * @return lista di WorkDto con totalMinutes valorizzato
     */
    @GetMapping("/manual/details/{orderId}/inprogress")
    public List<WorkDto> getManualWorksWithTotalMinutesByOrderInProgress(@PathVariable Long orderId) {
    	 List<WorkDto> works = workService.getManualWorksWithTotalMinutesByOrderInProgress(orderId);
        return works;
    }

    
    /**
     * Recupera i lavori manuali attivi per un ordine specifico IN CORSO,
     * includendo il totale dei minuti di esecuzione per ogni gruppo.
     *
     * @param orderId ID dell'ordine
     * @return lista di WorkDto con totalMinutes valorizzato
     */
    @GetMapping("/manual/details/{orderId}")
    public List<WorkDto> getManualWorksWithTotalMinutesByOrder(@PathVariable Long orderId) {
    	 List<WorkDto> works = workService.getManualWorksWithTotalMinutesByOrder(orderId);
        return works;
    }
    
    
    /**
     * Recupera tutti gli operatori.
     */
    @GetMapping("/operators")
    public List<UserDto> getOperators() {
        return userService.getAllActiveOperators();
    }

    /* ========= UPDATE ========= */

    /**
     * Aggiorna un Work esistente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<WorkDto> updateWork(@PathVariable Long id, @RequestBody WorkDto dto) {
        return ResponseEntity.ok(workService.updateWork(id, dto));
    }

    /**
     * Aggiorna un Work lotto esistente.
     */
    @PutMapping("/lotto/{id}")
    public ResponseEntity<WorkDto> updateLotto(@PathVariable Long id, @RequestBody WorkDto dto) {
        return ResponseEntity.ok(workService.updateLottoWork(id, dto));
    }

    /* ========= DELETE ========= */

    /**
     * Elimina un Work.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWork(@PathVariable Long id) {
        workService.deleteWork(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Elimina un Work lotto.
     */
    @DeleteMapping("/lotto/{id}")
    public ResponseEntity<Void> deleteLotto(@PathVariable Long id) {
        workService.deleteLottoWork(id);
        return ResponseEntity.noContent().build();
    }

    /* ========= ACTIONS ========= */

    /**
     * Chiude un Work specificando lo stato.
     */
    @PutMapping("/{id}/close")
    public ResponseEntity<WorkDto> closeWork(@PathVariable Long id, @RequestParam WorkStatus status) {
        return ResponseEntity.ok(workService.closeWork(id, status));
    }

    /**
     * Transizione di stato di un Work.
     */
    @PostMapping("/{workId}/transition")
    public ResponseEntity<WorkDto> transitionWork(@PathVariable Long workId, @RequestBody Map<String, String> body) {
        String statusStr = body.get("status");
        if (statusStr == null) throw new IllegalArgumentException("Parametro 'status' mancante");

        WorkStatus status = WorkStatus.valueOf(statusStr.toUpperCase());
        return ResponseEntity.ok(workService.transitionWork(workId, status));
    }
    
    
    /**
     * Recupera tutti gli step (lavorazioni) collegati a una work.
     *
     * @param id ID della work principale
     * @return Lista di WorkDto o 404 se non trovati
     */
    @GetMapping("/{id}/steps")
    public ResponseEntity<List<WorkDto>> getStepsByWork(@PathVariable Long id) {
        List<WorkDto> steps = workService.getStepsByWork(id);

        if (steps == null || steps.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(steps);
    }
}
