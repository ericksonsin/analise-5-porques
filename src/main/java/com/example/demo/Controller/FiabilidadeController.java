package com.example.demo.Controller;

import com.example.demo.Modelo.Equipamento;
import com.example.demo.Modelo.Fiabilidade;
import com.example.demo.Modelo.FiabilidadeRegistro;
import com.example.demo.Modelo.StatusTurno;
import com.example.demo.Repository.EquipamentoRepository;
import com.example.demo.Repository.FiabilidadeRegistroRepository;
import com.example.demo.Repository.FiabilidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;

@Controller
@RequestMapping("/fiabilidade")
public class FiabilidadeController {

        @Autowired
        private FiabilidadeRepository fiabilidadeRepository;
        @Autowired
        private FiabilidadeRegistroRepository registroRepository;
        @Autowired
        private EquipamentoRepository equipamentoRepository;

        // Adiciona equipamentos a todos os modelos deste controller
        @ModelAttribute("todosEquipamentos")
        public List<Equipamento> todosEquipamentos() {
                return equipamentoRepository.findAll();
        }

        /**
         * Tela para criar um novo turno de trabalho.
         */
        @GetMapping("/novo")
        public String exibirFormularioNovo(Model model) {
                // Objeto para o formulário (pode ser de criação ou edição)
                if (!model.containsAttribute("turno")) { // Se não veio de um redirect de edição
                        model.addAttribute("turno", new Fiabilidade());
                }
                // Lista de turnos para a tabela
                model.addAttribute("listaDeTurnos",
                                fiabilidadeRepository.findAll(Sort.by(Sort.Direction.DESC, "data")));
                return "fiabilidade/novo";
        }

        @GetMapping("/editar/{id}")
        public String exibirFormularioEdicao(@PathVariable Long id, Model model) {
                Fiabilidade turno = fiabilidadeRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Turno não encontrado com ID: " + id));

                // Adiciona o turno a ser editado ao model
                model.addAttribute("turno", turno);

                // Adiciona a lista para a tabela
                model.addAttribute("listaDeTurnos",
                                fiabilidadeRepository.findAll(Sort.by(Sort.Direction.DESC, "data")));

                // Reutiliza a mesma view 'novo.html'
                return "fiabilidade/novo";
        }

        /**
         * Salva um novo turno. Agora com data selecionável.
         */
        @PostMapping("/salvar")
        public String salvar(Fiabilidade fiabilidade, RedirectAttributes redirectAttributes) {
                // Verifica se já existe um turno para este equipamento nesta data
                Optional<Fiabilidade> existente = fiabilidadeRepository
                                .findByEquipamentoIdAndData(fiabilidade.getEquipamento().getId(),
                                                fiabilidade.getData());
                if (existente.isPresent()) {
                        redirectAttributes.addFlashAttribute("mensagemErro",
                                        "Já existe um turno cadastrado para este equipamento na data selecionada.");
                        return "redirect:/fiabilidade/novo";
                }

                fiabilidade.setStatus(StatusTurno.ABERTO); // Garante que o status comece como ABERTO
                fiabilidadeRepository.save(fiabilidade);
                redirectAttributes.addFlashAttribute("mensagemSucesso", "Turno de trabalho criado com sucesso!");
                // Redireciona para a nova tela de apontamento
                return "redirect:/fiabilidade/novo";
        }

        @PostMapping("/atualizar/{id}")
        public String atualizarTurno(@PathVariable Long id, @ModelAttribute("turno") Fiabilidade turnoAtualizado,
                        RedirectAttributes redirectAttributes) {
                // Garante que o ID não seja perdido
                turnoAtualizado.setId(id);
                fiabilidadeRepository.save(turnoAtualizado);
                redirectAttributes.addFlashAttribute("mensagemSucesso", "Turno atualizado com sucesso!");
                return "redirect:/fiabilidade/novo";
        }

        /**
         * Exclui um turno.
         */
        @PostMapping("/excluir/{id}")
        public String excluirTurno(@PathVariable Long id, RedirectAttributes redirectAttributes) {
                try {
                        Fiabilidade turno = fiabilidadeRepository.findById(id)
                                        .orElseThrow(() -> new IllegalArgumentException(
                                                        "Turno não encontrado com ID: " + id));

                        List<FiabilidadeRegistro> registros = registroRepository.findByFiabilidadeId(id);
                        registroRepository.deleteAll(registros);
                        fiabilidadeRepository.delete(turno);

                        redirectAttributes.addFlashAttribute("mensagemSucesso",
                                        "Seus registros foram excluídos com sucesso.");
                } catch (Exception e) {
                        redirectAttributes.addFlashAttribute("mensagemErro",
                                        "Erro ao excluir o turno: " + e.getMessage());
                }
                return "redirect:/fiabilidade/novo";
        }

        /**
         * NOVA TELA DE APONTAMENTO - O coração do novo fluxo.
         */
        // @GetMapping("/apontamento")
        // public String apontamento(
        // @RequestParam(required = false) Long equipamentoId,
        // @RequestParam(required = false) @DateTimeFormat(iso =
        // DateTimeFormat.ISO.DATE) LocalDate data,
        // @RequestParam(required = false) Long editId, // <-- 1. NOVO PARÂMETRO para o
        // ID de edição
        // Model model) {

        // // A busca de equipamentos já é feita pelo
        // @ModelAttribute("todosEquipamentos"),
        // // então a linha abaixo não é mais necessária aqui.
        // // List<Equipamento> equipamentos = equipamentoRepository.findAll();
        // // model.addAttribute("equipamentos", equipamentos);

        // LocalDate dataBusca = (data == null) ? LocalDate.now() : data;

        // if (equipamentoId != null) {
        // Optional<Fiabilidade> optFiabilidade =
        // fiabilidadeRepository.findByEquipamentoIdAndData(
        // equipamentoId,
        // dataBusca);
        // if (optFiabilidade.isPresent()) {
        // Fiabilidade f = optFiabilidade.get();
        // model.addAttribute("fiabilidade", f);

        // List<FiabilidadeRegistro> registros =
        // registroRepository.findByFiabilidadeId(f.getId());

        // // --- 👇 LÓGICA DE CÁLCULO ATUALIZADA 👇 ---
        // int produzido = registros.stream()
        // .max(Comparator.comparing(FiabilidadeRegistro::getHoraRegistro)) // 1.
        // // Encontra
        // // o
        // // registro
        // // com
        // // a
        // // maior
        // // hora
        // .map(FiabilidadeRegistro::getQuantidade) // 2. Pega a quantidade DESSE
        // // registro
        // .orElse(0); // 3. Se não houver registros, o produzido é 0

        // model.addAttribute("registros", registros);
        // model.addAttribute("produzido", produzido);
        // model.addAttribute("faltando", f.getMetaProducao() - produzido);
        // model.addAttribute("fiabilidadePercentual",
        // (f.getMetaProducao() > 0) ? (produzido * 100.0) / f.getMetaProducao()
        // : 0);
        // }
        // }

        // // --- 2. LÓGICA ATUALIZADA PARA O FORMULÁRIO DE REGISTRO/EDIÇÃO ---
        // if (editId != null) {
        // // Se um 'editId' veio na URL, estamos em modo de edição. Busca o registro.
        // registroRepository.findById(editId)
        // .ifPresent(registroParaEditar -> model.addAttribute("novoRegistro",
        // registroParaEditar));
        // } else if (!model.containsAttribute("novoRegistro")) {
        // // Se não veio 'editId' e não veio de um redirect (como na criação), cria um
        // // novo registro vazio.
        // model.addAttribute("novoRegistro", new FiabilidadeRegistro());
        // }
        // // Se um 'novoRegistro' já existe no model (vindo de um redirect), não faz
        // nada,
        // // apenas o utiliza.

        // model.addAttribute("equipamentoSelecionado", equipamentoId);
        // model.addAttribute("dataSelecionada", dataBusca);

        // return "fiabilidade/apontamento";
        // }

        @GetMapping("/apontamento")
        public String apontamento(
                        @RequestParam(required = false) Long equipamentoId,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
                        @RequestParam(required = false) Long editId,
                        Model model) {

                LocalDate dataBusca = (data == null) ? LocalDate.now() : data;

                if (equipamentoId != null) {
                        Optional<Fiabilidade> optFiabilidade = fiabilidadeRepository
                                        .findByEquipamentoIdAndData(equipamentoId, dataBusca);

                        if (optFiabilidade.isPresent()) {
                                Fiabilidade f = optFiabilidade.get();
                                model.addAttribute("fiabilidade", f);

                                int produzido = registroRepository.findByFiabilidadeId(f.getId()).stream()
                                                .max(Comparator.comparing(FiabilidadeRegistro::getHoraRegistro))
                                                .map(FiabilidadeRegistro::getQuantidade)
                                                .orElse(0);

                                int metaTotalTurno = f.getMetaProducao();
                                LocalTime horaInicio = f.getHoraInicio();
                                LocalTime horaFim = f.getHoraFim();
                                LocalTime horaAtual = LocalTime.now();
                                Integer capacidadeMaquina = f.getEquipamento().getProducaoPorHora();

                                double ritmoMedioNecessario = 0;
                                double ritmoParaBaterMeta = 0;
                                boolean isPossivelBaterMeta = true;

                                Duration duracaoTotalTurno = Duration.between(horaInicio, horaFim);
                                double horasTotaisTurno = duracaoTotalTurno.toMinutes() / 60.0;

                                if (horasTotaisTurno > 0) {
                                        ritmoMedioNecessario = (double) metaTotalTurno / horasTotaisTurno;
                                }

                                int producaoRestante = metaTotalTurno - produzido;

                                if (producaoRestante <= 0) {
                                        isPossivelBaterMeta = true;
                                        ritmoParaBaterMeta = 0;
                                } else {
                                        if (horaAtual.isAfter(horaFim) || horaAtual.equals(horaFim)) {
                                                isPossivelBaterMeta = false;
                                                ritmoParaBaterMeta = Double.POSITIVE_INFINITY;
                                        } else {
                                                Duration duracaoRestante = Duration.between(horaAtual, horaFim);
                                                double horasRestantes = duracaoRestante.toMinutes() / 60.0;

                                                if (horasRestantes > 0) {
                                                        ritmoParaBaterMeta = producaoRestante / horasRestantes;
                                                        if (capacidadeMaquina != null && capacidadeMaquina > 0
                                                                        && ritmoParaBaterMeta > capacidadeMaquina) {
                                                                isPossivelBaterMeta = false;
                                                        }
                                                } else {
                                                        isPossivelBaterMeta = false;
                                                }
                                        }
                                }

                                model.addAttribute("ritmoMedioNecessario", ritmoMedioNecessario);
                                model.addAttribute("ritmoParaBaterMeta", ritmoParaBaterMeta);
                                model.addAttribute("isPossivelBaterMeta", isPossivelBaterMeta);

                                model.addAttribute("registros", registroRepository.findByFiabilidadeId(f.getId()));
                                model.addAttribute("produzido", produzido);
                                model.addAttribute("faltando", metaTotalTurno - produzido);
                                model.addAttribute("fiabilidadePercentual",
                                                (metaTotalTurno > 0) ? (produzido * 100.0) / metaTotalTurno : 0);
                        }
                }

                if (editId != null) {
                        // Se um 'editId' veio na URL, estamos em modo de edição. Busca o registro.
                        registroRepository.findById(editId)
                                        .ifPresent(registroParaEditar -> model.addAttribute("novoRegistro",
                                                        registroParaEditar));
                } else if (!model.containsAttribute("novoRegistro")) {
                        // Se não veio 'editId' e não veio de um redirect (como na criação), cria um
                        // novo registro vazio.
                        model.addAttribute("novoRegistro", new FiabilidadeRegistro());
                }

                model.addAttribute("equipamentoSelecionado", equipamentoId);
                model.addAttribute("dataSelecionada", dataBusca);

                return "fiabilidade/apontamento";
        }

        /**
         * Registra uma nova quantidade de produção.
         */
        // @PostMapping("/registrar")
        // public String registrar(FiabilidadeRegistro registro, @RequestParam Long
        // fiabilidadeId,
        // RedirectAttributes redirectAttributes) {

        // Fiabilidade fiabilidadeDoRegistroAtual =
        // fiabilidadeRepository.findById(fiabilidadeId)
        // .orElseThrow(() -> new IllegalArgumentException("Fiabilidade não
        // encontrada"));

        // // --- 👇 NOVA LÓGICA DE VALIDAÇÃO 👇 ---

        // // 1. Pega a data do turno onde o registro está sendo feito.
        // LocalDate dataDoTurnoAtual = fiabilidadeDoRegistroAtual.getData();
        // LocalDate diaAnterior = dataDoTurnoAtual.minusDays(1);

        // // 2. Busca todos os turnos do dia anterior.
        // List<Fiabilidade> turnosDoDiaAnterior =
        // fiabilidadeRepository.findByData(diaAnterior);

        // // 3. Verifica se ALGUM turno do dia anterior ainda está com status ABERTO.
        // boolean existeTurnoAnteriorAberto = turnosDoDiaAnterior.stream()
        // .anyMatch(turno -> turno.getStatus() == StatusTurno.ABERTO);

        // // 4. Se existir, bloqueia a operação e envia uma mensagem de erro.
        // if (existeTurnoAnteriorAberto) {
        // redirectAttributes.addFlashAttribute("mensagemErro",
        // "Não é possível registrar produção. Existem turnos do dia anterior ("
        // + diaAnterior.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        // + ") que ainda não foram finalizados.");

        // // Redireciona de volta para a mesma tela de apontamento, sem salvar.
        // return "redirect:/fiabilidade/apontamento?equipamentoId="
        // + fiabilidadeDoRegistroAtual.getEquipamento().getId() + "&data="
        // + fiabilidadeDoRegistroAtual.getData();
        // }

        // // --- FIM DA VALIDAÇÃO ---

        // // Se passou pela validação, o código original continua.
        // // Impede registro em turno já finalizado (validação que você já tinha).
        // if (fiabilidadeDoRegistroAtual.getStatus() == StatusTurno.FINALIZADO) {
        // redirectAttributes.addFlashAttribute("mensagemErro",
        // "Não é possível adicionar registros a um turno já finalizado.");
        // } else {
        // registro.setFiabilidade(fiabilidadeDoRegistroAtual);
        // registroRepository.save(registro);
        // redirectAttributes.addFlashAttribute("mensagemSucesso", "Registro de produção
        // salvo!");
        // }

        // // Redireciona de volta para a tela de apontamento com os filtros corretos.
        // return "redirect:/fiabilidade/apontamento?equipamentoId=" +
        // fiabilidadeDoRegistroAtual.getEquipamento().getId()
        // + "&data=" + fiabilidadeDoRegistroAtual.getData();
        // }

        @PostMapping("/registrar")
        public String registrar(FiabilidadeRegistro novoRegistro, // Alterado para receber o objeto completo
                        @RequestParam Long fiabilidadeId,
                        RedirectAttributes redirectAttributes) {

                Fiabilidade fiabilidadeDoRegistroAtual = fiabilidadeRepository.findById(fiabilidadeId)
                                .orElseThrow(() -> new IllegalArgumentException("Fiabilidade não encontrada"));

                // --- VALIDAÇÃO 1: DIA ANTERIOR (lógica que já existe) ---
                LocalDate dataDoTurnoAtual = fiabilidadeDoRegistroAtual.getData();
                LocalDate diaAnterior = dataDoTurnoAtual.minusDays(1);
                boolean existeTurnoAnteriorAberto = fiabilidadeRepository.findByData(diaAnterior).stream()
                                .anyMatch(turno -> turno.getStatus() == StatusTurno.ABERTO);

                if (existeTurnoAnteriorAberto) {
                        redirectAttributes.addFlashAttribute("mensagemErro",
                                        "Não é possível registrar produção. Existem turnos do dia anterior ("
                                                        + diaAnterior.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                                        + ") que ainda não foram finalizados.");
                        return "redirect:/fiabilidade/apontamento?equipamentoId="
                                        + fiabilidadeDoRegistroAtual.getEquipamento().getId() + "&data="
                                        + fiabilidadeDoRegistroAtual.getData();
                }

                // --- 👇 NOVA LÓGICA DE VALIDAÇÃO DE ODÔMETRO 👇 ---

                // 1. Busca todos os registros existentes para este turno.
                List<FiabilidadeRegistro> registrosExistentes = registroRepository.findByFiabilidadeId(fiabilidadeId);

                // 2. Encontra o último registro (o que tem a maior hora).
                Optional<FiabilidadeRegistro> ultimoRegistroOpt = registrosExistentes.stream()
                                .max(Comparator.comparing(FiabilidadeRegistro::getHoraRegistro));

                // 3. Se um último registro existe, compara a quantidade.
                if (ultimoRegistroOpt.isPresent()) {
                        FiabilidadeRegistro ultimoRegistro = ultimoRegistroOpt.get();
                        // 4. Se a nova quantidade for menor que a anterior, bloqueia.
                        if (novoRegistro.getQuantidade() < ultimoRegistro.getQuantidade()) {
                                redirectAttributes.addFlashAttribute("mensagemErro",
                                                "Erro: A produção informada (" + novoRegistro.getQuantidade()
                                                                + ") é menor que a última registrada ("
                                                                + ultimoRegistro.getQuantidade() + ").");

                                // Redireciona de volta, sem salvar.
                                return "redirect:/fiabilidade/apontamento?equipamentoId="
                                                + fiabilidadeDoRegistroAtual.getEquipamento().getId() + "&data="
                                                + fiabilidadeDoRegistroAtual.getData();
                        }
                }

                // --- FIM DA VALIDAÇÃO DE ODÔMETRO ---

                // Se passou por todas as validações, o código original continua.
                if (fiabilidadeDoRegistroAtual.getStatus() == StatusTurno.FINALIZADO) {
                        redirectAttributes.addFlashAttribute("error",
                                        "Não é possível adicionar registros a um turno já finalizado.");
                } else {
                        novoRegistro.setFiabilidade(fiabilidadeDoRegistroAtual);
                        registroRepository.save(novoRegistro);
                        redirectAttributes.addFlashAttribute("success", "Registro de produção salvo!");
                }

                return "redirect:/fiabilidade/apontamento?equipamentoId="
                                + fiabilidadeDoRegistroAtual.getEquipamento().getId() + "&data="
                                + fiabilidadeDoRegistroAtual.getData();
        }

        @GetMapping("/registro/editar/{id}")
        public String editarRegistro(@PathVariable Long id, RedirectAttributes redirectAttributes) {
                FiabilidadeRegistro registro = registroRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Registro não encontrado com ID: " + id));

                Fiabilidade fiabilidade = registro.getFiabilidade();

                // Passa o registro a ser editado para a próxima requisição
                redirectAttributes.addFlashAttribute("novoRegistro", registro);

                // Adiciona os parâmetros de filtro ao redirect para que não se percam
                redirectAttributes.addAttribute("equipamentoId", fiabilidade.getEquipamento().getId());
                redirectAttributes.addAttribute("data", fiabilidade.getData().toString()); // Envia como string

                // Redireciona para a tela de apontamento
                return "redirect:/fiabilidade/apontamento";
        }

        /**
         * Processa a ATUALIZAÇÃO de um registro de produção.
         */
        @PostMapping("/registro/atualizar/{id}")
        public String atualizarRegistro(@PathVariable Long id,
                        @ModelAttribute("novoRegistro") FiabilidadeRegistro registroAtualizado,
                        @RequestParam Long fiabilidadeId,
                        RedirectAttributes redirectAttributes) {

                Fiabilidade fiabilidade = fiabilidadeRepository.findById(fiabilidadeId)
                                .orElseThrow(() -> new IllegalArgumentException("Fiabilidade não encontrada"));

                // Carrega o registro original para preservar dados não presentes no form
                FiabilidadeRegistro registroOriginal = registroRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Registro original não encontrado"));

                registroOriginal.setDataApontamento(registroAtualizado.getDataApontamento());
                registroOriginal.setHoraRegistro(registroAtualizado.getHoraRegistro());
                registroOriginal.setQuantidade(registroAtualizado.getQuantidade());
                // A fiabilidade já está associada

                registroRepository.save(registroOriginal);
                redirectAttributes.addFlashAttribute("success", "Registro atualizado com sucesso!");

                // Adiciona os parâmetros de filtro ao redirect
                redirectAttributes.addAttribute("equipamentoId", fiabilidade.getEquipamento().getId());
                redirectAttributes.addAttribute("data", fiabilidade.getData().toString());

                return "redirect:/fiabilidade/apontamento";
        }

        /**
         * Exclui um registro de produção.
         */
        @PostMapping("/registro/excluir/{id}")
        public String excluirRegistro(@PathVariable Long id, RedirectAttributes redirectAttributes) {
                FiabilidadeRegistro registro = registroRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Registro não encontrado com ID: " + id));

                Fiabilidade fiabilidade = registro.getFiabilidade();
                registroRepository.delete(registro);

                redirectAttributes.addFlashAttribute("success", "Registro excluído com sucesso.");

                return "redirect:/fiabilidade/apontamento?equipamentoId=" + fiabilidade.getEquipamento().getId()
                                + "&data="
                                + fiabilidade.getData();
        }

        /**
         * Finaliza um turno de trabalho.
         */
        @PostMapping("/finalizar/{id}")
        public String finalizarTurno(@PathVariable Long id, RedirectAttributes redirectAttributes) {
                Fiabilidade fiabilidade = fiabilidadeRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Fiabilidade não encontrada"));

                fiabilidade.setStatus(StatusTurno.FINALIZADO);
                fiabilidade.setDataFinalizacao(LocalDateTime.now());
                fiabilidadeRepository.save(fiabilidade);

                redirectAttributes.addFlashAttribute("mensagemSucesso", "Turno finalizado com sucesso!");
                return "redirect:/fiabilidade/apontamento?equipamentoId=" + fiabilidade.getEquipamento().getId()
                                + "&data="
                                + fiabilidade.getData();
        }

        @GetMapping("/dashboard")
        public String dashboard(
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
                        Model model) {

                // Se nenhuma data for fornecida, usa a data de hoje.
                LocalDate dataBusca = (data == null) ? LocalDate.now() : data;

                // 1. Busca todos os turnos (Fiabilidade) para a data selecionada.
                List<Fiabilidade> turnosDoDia = fiabilidadeRepository.findByData(dataBusca);

                List<Map<String, Object>> dadosDashboard = new ArrayList<>();
                int metaTotal = 0;
                int produzidoTotal = 0;

                // 2. Itera sobre cada turno para calcular os dados individuais.
                for (Fiabilidade turno : turnosDoDia) {
                        List<FiabilidadeRegistro> registros = registroRepository.findByFiabilidadeId(turno.getId());

                        // --- LÓGICA DE CÁLCULO CORRIGIDA ---
                        // Encontra o valor do último registro (maior hora) em vez de somar todos.
                        int produzidoNoTurno = registros.stream()
                                        .max(Comparator.comparing(FiabilidadeRegistro::getHoraRegistro)) // Encontra o
                                                                                                         // registro com
                                                                                                         // a maior
                                                                                                         // hora
                                        .map(FiabilidadeRegistro::getQuantidade) // Pega a quantidade DESSE registro
                                        .orElse(0); // Se não houver registros, o produzido é 0

                        int metaDoTurno = turno.getMetaProducao();
                        int faltando = metaDoTurno - produzidoNoTurno;
                        double fiabilidadePercentual = (metaDoTurno > 0) ? (produzidoNoTurno * 100.0) / metaDoTurno : 0;

                        // Adiciona os dados individuais à lista para a tabela
                        Map<String, Object> linha = new HashMap<>();
                        linha.put("equipamento", turno.getEquipamento().getNome());
                        linha.put("meta", metaDoTurno);
                        linha.put("produzido", produzidoNoTurno);
                        linha.put("faltando", faltando);
                        linha.put("fiabilidade", fiabilidadePercentual);
                        dadosDashboard.add(linha);

                        // Acumula os totais
                        metaTotal += metaDoTurno;
                        // O "produzidoTotal" agora é a soma das últimas leituras de cada equipamento
                        produzidoTotal += produzidoNoTurno;
                }

                // Calcula a fiabilidade geral com base nos totais corretos
                double fiabilidadeGeral = (metaTotal > 0) ? (produzidoTotal * 100.0) / metaTotal : 0;

                // Adiciona tudo ao Model para a view usar
                model.addAttribute("dashboard", dadosDashboard);
                model.addAttribute("dataSelecionada", dataBusca);
                model.addAttribute("metaTotal", metaTotal);
                model.addAttribute("produzidoTotal", produzidoTotal);
                model.addAttribute("fiabilidadeGeral", fiabilidadeGeral);

                return "fiabilidade/dashboard";
        }

        @GetMapping("/graficos")
        public String exibirPaginaGraficos() {
                return "fiabilidade/graficos";
        }

        @GetMapping("/dados-grafico-diario") // A URL da API
        @ResponseBody
        public Map<String, Object> getDadosGrafico(
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {

                // 1. Busca todos os turnos dentro do período selecionado.
                List<Fiabilidade> turnosNoPeriodo = fiabilidadeRepository.findByDataBetween(dataInicio, dataFim);

                Map<String, Object> dadosGrafico = new HashMap<>();

                // --- DADOS PARA O GRÁFICO DE BARRAS (Produção Final por Equipamento no
                // Período) ---
                // Agrupa os turnos por equipamento
                Map<Equipamento, List<Fiabilidade>> turnosPorEquipamento = turnosNoPeriodo.stream()
                                .collect(Collectors.groupingBy(Fiabilidade::getEquipamento));

                List<String> nomesEquipamentos = new ArrayList<>();
                List<Integer> producaoTotalPorEquipamento = new ArrayList<>();

                turnosPorEquipamento.forEach((equipamento, turnos) -> {
                        nomesEquipamentos.add(equipamento.getNome());
                        // Para cada equipamento, soma a produção final de cada um de seus turnos no
                        // período
                        int producaoTotal = turnos.stream()
                                        .mapToInt(turno -> registroRepository.findByFiabilidadeId(turno.getId())
                                                        .stream()
                                                        .max(Comparator.comparing(FiabilidadeRegistro::getHoraRegistro))
                                                        .map(FiabilidadeRegistro::getQuantidade)
                                                        .orElse(0))
                                        .sum();
                        producaoTotalPorEquipamento.add(producaoTotal);
                });
                dadosGrafico.put("labelsBarras", nomesEquipamentos);
                dadosGrafico.put("dataBarras", producaoTotalPorEquipamento);

                // --- DADOS PARA O GRÁFICO DE LINHA (Produção Total por Dia) ---
                // Agrupa os turnos por data
                Map<LocalDate, List<Fiabilidade>> turnosPorDia = turnosNoPeriodo.stream()
                                .collect(Collectors.groupingBy(Fiabilidade::getData));

                // Usamos um LinkedHashMap para manter a ordem das datas
                Map<LocalDate, Integer> producaoTotalPorDia = new LinkedHashMap<>();

                // Itera sobre cada dia do período para garantir que todos os dias apareçam no
                // gráfico
                for (LocalDate dia = dataInicio; !dia.isAfter(dataFim); dia = dia.plusDays(1)) {
                        List<Fiabilidade> turnosDoDia = turnosPorDia.get(dia);
                        int producaoDoDia = 0;
                        if (turnosDoDia != null) {
                                // Para cada turno no dia, pega a última leitura e soma
                                producaoDoDia = turnosDoDia.stream()
                                                .mapToInt(turno -> registroRepository.findByFiabilidadeId(turno.getId())
                                                                .stream()
                                                                .max(Comparator.comparing(
                                                                                FiabilidadeRegistro::getHoraRegistro))
                                                                .map(FiabilidadeRegistro::getQuantidade)
                                                                .orElse(0))
                                                .sum();
                        }
                        producaoTotalPorDia.put(dia, producaoDoDia);
                }

                // Formata os dados para o Chart.js
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
                List<String> labelsLinha = producaoTotalPorDia.keySet().stream()
                                .map(date -> date.format(formatter))
                                .collect(Collectors.toList());
                List<Integer> dataLinha = new ArrayList<>(producaoTotalPorDia.values());

                dadosGrafico.put("labelsLinha", labelsLinha);
                dadosGrafico.put("dataLinha", dataLinha);

                return dadosGrafico;
        }

}
