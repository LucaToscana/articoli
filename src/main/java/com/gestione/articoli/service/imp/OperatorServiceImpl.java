package com.gestione.articoli.service.imp;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.gestione.articoli.dto.InactiveUsersDto;
import com.gestione.articoli.dto.OperatorDto;
import com.gestione.articoli.model.Role;
import com.gestione.articoli.model.User;
import com.gestione.articoli.model.Work;
import com.gestione.articoli.model.WorkActivityType;
import com.gestione.articoli.model.WorkStatus;
import com.gestione.articoli.repository.UserRepository;
import com.gestione.articoli.repository.WorkRepository;
import com.gestione.articoli.service.OperatorService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OperatorServiceImpl implements OperatorService {

	private final UserRepository userRepository;
	private final WorkRepository workRepository;

	@Override
	public List<OperatorDto> getAllOperators() {
		return userRepository.findAll().stream()
				.map(op -> OperatorDto.builder().id(op.getId()).username(op.getUsername())
						.activeInCompany(op.isActiveInCompany()).machineUser(op.isMachineUser())
						.retribuzioneOraria(op.getRetribuzioneOraria()).build())
				.collect(Collectors.toList());
	}

	@Override
	public OperatorDto getOperatorById(Long id) {
		User op = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Operatore non trovato"));
		return OperatorDto.builder().id(op.getId()).username(op.getUsername()).activeInCompany(op.isActiveInCompany())
				.machineUser(op.isMachineUser()).retribuzioneOraria(op.getRetribuzioneOraria()).build();
	}

	@Override
	public void deactivateOperator(Long id) {
		User op = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Operatore non trovato"));
		op.setActiveInCompany(false);
		userRepository.save(op);
	}

	@Override
	public void deleteOperator(Long id) {
		var operator = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Operatore non trovato"));

		// Username speciali
		if (isSpecialUsername(operator.getUsername())) {
			throw new RuntimeException("Non puoi eliminare questo operatore!");
		}

		// Controlla se è assegnato a un Work come operator1/2/3 o manager
		boolean isAssigned = workRepository.existsByOperatorOrOperator2OrOperator3OrManager(operator, operator,
				operator, operator);

		if (isAssigned) {
			throw new RuntimeException("Impossibile eliminare l'operatore: è assegnato ad almeno un lavoro");
		}

		userRepository.deleteById(id);
	}

	@Override
	public OperatorDto updateOperator(Long id, OperatorDto operatorDto) {
		User existing = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Operatore non trovato"));

		if (operatorDto.getRetribuzioneOraria() != null) {
			existing.setRetribuzioneOraria(operatorDto.getRetribuzioneOraria());
		}

		if (operatorDto.getActiveInCompany() != null) {
			existing.setActiveInCompany(operatorDto.getActiveInCompany());
			if (existing.getRoles().isEmpty()) {
				List<Work> activeWorks = workRepository.findByOperatorAndStatusAndEndTimeIsNull(existing,
						WorkStatus.IN_PROGRESS);

				boolean hasRelevantWork = activeWorks.stream()
						.anyMatch(work -> work.getActivity() != WorkActivityType.DISPONIBILITA_LOTTO
								&& work.getActivity() != WorkActivityType.DISPONIBILITA_LAVORAZIONE);

				if (hasRelevantWork) {
					throw new RuntimeException(
							"Errore durante aggiornamento operatore. Potrebbe essere presente in lavorazioni in corso. In caso prova a chiuderle e riprova.");
				}
			}
			if (existing.getRoles().contains(Role.ADMIN)) {
			    long adminCount = userRepository.countActiveByRole(Role.ADMIN);
			    if (adminCount < 5) {
			        throw new RuntimeException(
			            "Impossibile disattivare questo admin: nel database devono essere presenti un minimo numero di admin attivi. Creane altri e ritenta."
			        );
			    }		
			}
		}

		User updated = userRepository.save(existing);

		return OperatorDto.builder().id(updated.getId()).username(updated.getUsername())
				.activeInCompany(updated.isActiveInCompany()).machineUser(updated.isMachineUser())
				.retribuzioneOraria(updated.getRetribuzioneOraria()).build();
	}

	// 🔹 Metodo helper per controllare username speciali
	private boolean isSpecialUsername(String username) {
		if (username == null)
			return false;
		String lower = username.toLowerCase();
		return lower.equals("admin") || lower.equals("user") || lower.equals("manager") || lower.equals("admin_lt");
	}

}
