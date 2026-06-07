package br.com.fiap.amanaje.clientes.service;

import br.com.fiap.amanaje.clientes.enums.TipoCliente;

import br.com.fiap.amanaje.clientes.repository.ClienteRepository;

import br.com.fiap.amanaje.clientes.model.Cliente;

import java.time.LocalDateTime;
import java.util.List;

import br.com.fiap.amanaje.clientes.dto.ClienteCreateRequest;
import br.com.fiap.amanaje.clientes.dto.ClienteResponse;
import br.com.fiap.amanaje.clientes.dto.ClienteUpdateRequest;
import br.com.fiap.amanaje.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteService {

	private static final String ATIVO = "S";
	private static final String INATIVO = "N";

	private final ClienteRepository clienteRepository;

	public ClienteService(ClienteRepository clienteRepository) {
		this.clienteRepository = clienteRepository;
	}

	@Transactional
	public ClienteResponse criar(ClienteCreateRequest request) {
		LocalDateTime agora = LocalDateTime.now();
		Cliente cliente = Cliente.builder()
				.nome(request.nome())
				.tipoCliente(request.tipoCliente())
				.documento(request.documento())
				.emailContato(request.emailContato())
				.telefone(request.telefone())
				.stAtivo(ATIVO)
				.dtCriadoEm(agora)
				.build();

		return toResponse(clienteRepository.save(cliente));
	}

	@Transactional(readOnly = true)
	public List<ClienteResponse> listarAtivos() {
		return clienteRepository.findByStAtivo(ATIVO).stream()
				.map(this::toResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public ClienteResponse buscarPorId(Long idCliente) {
		return toResponse(buscarAtivo(idCliente));
	}

	@Transactional
	public ClienteResponse atualizar(Long idCliente, ClienteUpdateRequest request) {
		Cliente cliente = buscarAtivo(idCliente);
		cliente.setNome(request.nome());
		cliente.setTipoCliente(request.tipoCliente());
		cliente.setDocumento(request.documento());
		cliente.setEmailContato(request.emailContato());
		cliente.setTelefone(request.telefone());
		cliente.setDtAtualizadoEm(LocalDateTime.now());

		return toResponse(clienteRepository.save(cliente));
	}

	@Transactional
	public void inativar(Long idCliente) {
		Cliente cliente = buscarAtivo(idCliente);
		LocalDateTime agora = LocalDateTime.now();
		cliente.setStAtivo(INATIVO);
		cliente.setDtDelEm(agora);
		cliente.setDtAtualizadoEm(agora);
		clienteRepository.save(cliente);
	}

	@Transactional(readOnly = true)
	public Cliente buscarAtivo(Long idCliente) {
		return clienteRepository.findById(idCliente)
				.filter(cliente -> ATIVO.equals(cliente.getStAtivo()))
				.orElseThrow(() -> new ResourceNotFoundException("Cliente ativo não encontrado: " + idCliente));
	}

	private ClienteResponse toResponse(Cliente cliente) {
		return new ClienteResponse(
				cliente.getIdCliente(),
				cliente.getNome(),
				cliente.getTipoCliente(),
				cliente.getDocumento(),
				cliente.getEmailContato(),
				cliente.getTelefone(),
				cliente.getStAtivo(),
				cliente.getDtCriadoEm(),
				cliente.getDtAtualizadoEm());
	}

}
