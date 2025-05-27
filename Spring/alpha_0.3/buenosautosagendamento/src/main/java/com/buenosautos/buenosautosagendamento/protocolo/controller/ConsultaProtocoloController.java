package com.buenosautos.buenosautosagendamento.protocolo.controller; // Ou em um pacote mais específico se preferir, como .protocolo.controller

import com.buenosautos.buenosautosagendamento.protocolo.model.Protocolo;
import com.buenosautos.buenosautosagendamento.protocolo.service.ProtocoloService;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/protocolos")
public class ConsultaProtocoloController { // Renomeado para evitar conflito com ProtocoloController existente

    @Autowired
    private ProtocoloService protocoloService;

    // Endpoint para exibir a página de formulário de consulta
    @GetMapping("/consultar")
    public String showConsultaForm() {
        return "protocolos/consultar-protocolo"; // Nome do novo template HTML
    }

    // Endpoint para processar a consulta do protocolo
    @PostMapping("/consultar")
    public String consultarProtocolo(@RequestParam("numeroProtocolo") String numeroProtocolo, Model model) {
        Optional<Protocolo> optionalProtocolo = protocoloService.buscarProtocoloPorNumero(numeroProtocolo);

        if (optionalProtocolo.isPresent()) {
            Protocolo foundProtocolo = optionalProtocolo.get();
            model.addAttribute("protocolo", foundProtocolo);
            // Os status options podem ser úteis para exibição, mesmo que o form seja desabilitado
            model.addAttribute("statusOptions", java.util.Arrays.stream(Protocolo.Status.values()).collect(java.util.stream.Collectors.toList()));
            return "protocolos/detalhes-protocolo-consulta";
        } else {
            model.addAttribute("mensagemErro", "Protocolo com número " + numeroProtocolo + " não encontrado.");
            return "protocolos/consultar-protocolo";
        }
    }
}