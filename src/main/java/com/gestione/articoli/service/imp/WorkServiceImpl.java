package com.gestione.articoli.service.imp;

import com.gestione.articoli.dto.TotalWorkTimeDto;
import com.gestione.articoli.dto.WorkDto;
import com.gestione.articoli.mapper.WorkMapper;
import com.gestione.articoli.model.Work;
import com.gestione.articoli.model.WorkStatus;
import com.gestione.articoli.model.OrdineArticolo;
import com.gestione.articoli.repository.WorkRepository;
import com.gestione.articoli.repository.OrdineArticoloRepository;
import com.gestione.articoli.service.WorkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkServiceImpl implements WorkService {

	private final WorkRepository workRepository;
	private final OrdineArticoloRepository ordineArticoloRepository;

	@Override
	public WorkDto createWork(WorkDto dto) {
		Work work = WorkMapper.toEntity(dto);
		if (dto.getOrderArticleId() != null) {
			OrdineArticolo oa = ordineArticoloRepository.findById(dto.getOrderArticleId())
					.orElseThrow(() -> new RuntimeException("OrdineArticolo non trovato"));
			work.setOrderArticle(oa);
		}
		return WorkMapper.toDto(workRepository.save(work));
	}

	@Override
	public WorkDto updateWork(Long id, WorkDto dto) {
		Work work = workRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Work non trovato con id " + id));

		work.setStatus(dto.getStatus() != null ? WorkStatus.valueOf(dto.getStatus()) : work.getStatus());
		work.setStartTime(dto.getStartTime());
		work.setEndTime(dto.getEndTime());

		return WorkMapper.toDto(workRepository.save(work));
	}

	@Override
	public void deleteWork(Long id) {
		if (!workRepository.existsById(id)) {
			throw new RuntimeException("Work non trovato con id " + id);
		}
		workRepository.deleteById(id);
	}

	@Override
	public WorkDto getWorkById(Long id) {
		Work work = workRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Work non trovato con id " + id));
		return WorkMapper.toDto(work);
	}

	@Override
	public List<WorkDto> getAllWorks() {
		return workRepository.findAll().stream().map(WorkMapper::toDto).collect(Collectors.toList());
	}

	@Override
	public List<WorkDto> getWorksByOrderArticle(Long orderArticleId) {
		OrdineArticolo oa = ordineArticoloRepository.findById(orderArticleId)
				.orElseThrow(() -> new RuntimeException("OrdineArticolo non trovato con id " + orderArticleId));
		return workRepository.findByOrderArticle(oa).stream().map(WorkMapper::toDto).collect(Collectors.toList());
	}

	@Override
	public long calculateTotalWorkSeconds(Long orderArticleId) {
		OrdineArticolo oa = ordineArticoloRepository.findById(orderArticleId)
				.orElseThrow(() -> new RuntimeException("OrdineArticolo non trovato con id " + orderArticleId));

		List<WorkStatus> excluded = List.of(WorkStatus.PAUSED, WorkStatus.CANCELLED);

		Long totalSeconds = workRepository.getTotalWorkDurationInSeconds(oa, excluded);
		return totalSeconds != null ? totalSeconds : 0L;
	}

	public TotalWorkTimeDto getTotalWorkTimeDto(Long orderArticleId) {
		long totalSec = calculateTotalWorkSeconds(orderArticleId);
		long hours = totalSec / 3600;
		long minutes = (totalSec % 3600) / 60;
		long seconds = totalSec % 60;
		return TotalWorkTimeDto.builder().hours(hours).minutes(minutes).seconds(seconds).build();
	}
}
