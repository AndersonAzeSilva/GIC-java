package br.com.megacenter.screens;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import br.com.megacenter.dal.ModuloConexao;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import java.awt.Dimension;
import javax.swing.Timer;

// ===== JFreeChart =====
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

/**
 * ===================================================== TELA DE DASHBOARD DO
 * SISTEMA ===================================================== Exibe: - Cards
 * com totais - Gráfico de Barras (Equipamentos por Tipo) - Gráfico de Pizza
 * (Equipamentos por Status)
 *
 * Os gráficos possuem tamanho fixo (460x260) e não extrapolam o layout da tela.
 */
public class ScreenDashboard extends javax.swing.JInternalFrame {

    // =========================
    // CONEXÃO
    // =========================
    private Connection conexao = ModuloConexao.conector();

    // =========================
    // GRÁFICOS
    // =========================
    private JFreeChart chartBarra;
    private JFreeChart chartPizza;

    // =========================
    // TIMER
    // =========================
    private Timer timerAtualizacao;

    // =====================================================
    // TIMERS DE ATUALIZAÇÃO (DADOS)
    // =====================================================
    private Timer timerAtualizacaoCards;          // Cards (5s)
    private Timer timerAtualizacaoGraficoBarra;   // Gráfico Barra (1 min)
    private Timer timerAtualizacaoGraficoPizza;   // Gráfico Pizza (1 min)

    // =====================================================
    // CONFIGURAÇÃO DE TEMPOS (ALTERE AQUI 👇)
    // =====================================================
    private static final int TEMPO_ATUALIZA_CARDS = 50000;       // 50 segundos
    private static final int TEMPO_ATUALIZA_GRAFICOS = 60000;  // 1 minuto

    // =====================================================
    // CONFIGURAÇÃO DE ANIMAÇÕES
    // =====================================================
    private static final int ANIMACAO_CARD = 15;     // ms
    private static final int ANIMACAO_BARRA = 30;    // ms
    private static final int PASSOS_BARRA = 100;
    private static final int ANIMACAO_PIZZA = 50;    // ms

    // =========================
    // TAMANHO DINÂMICO
    // =========================
    private Dimension tamanhoGrafico = new Dimension(460, 260);

    // =========================
    // CONSTRUTOR
    // =========================
    public ScreenDashboard() {
        initComponents();

        // Layouts dos painéis
        jpBarra.setLayout(new BorderLayout());
        jpPizza.setLayout(new BorderLayout());

        // Primeira carga imediata
        atualizarCards();
        carregarGraficoBarra();
        carregarGraficoPizza();

        // Inicia timers separados
        iniciarAtualizacaoCards();
        iniciarAtualizacaoGraficos();

        // Redesenha ao redimensionar
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                redesenharGraficos();
            }
        });
    }

    // =========================
    // TIMER AUTOMÁTICO
    // =========================
    private void iniciarAtualizacaoAutomatica() {
        timerAtualizacao = new Timer(5000, e -> atualizarDashboardCompleto());
        timerAtualizacao.start();
    }

    // =====================================================
    // TIMER DOS CARDS (5 SEGUNDOS)
    // =====================================================
    private void iniciarAtualizacaoCards() {
        timerAtualizacaoCards = new Timer(
                TEMPO_ATUALIZA_CARDS,
                e -> atualizarCards()
        );
        timerAtualizacaoCards.start();
    }

    // =====================================================
    // TIMER DOS GRÁFICOS (1 MINUTO)
    // =====================================================
    private void iniciarAtualizacaoGraficos() {

        // Gráfico de Barras
        timerAtualizacaoGraficoBarra = new Timer(
                TEMPO_ATUALIZA_GRAFICOS,
                e -> carregarGraficoBarra()
        );
        timerAtualizacaoGraficoBarra.start();

        // Gráfico de Pizza
        timerAtualizacaoGraficoPizza = new Timer(
                TEMPO_ATUALIZA_GRAFICOS,
                e -> carregarGraficoPizza()
        );
        timerAtualizacaoGraficoPizza.start();
    }

    @Override
    public void dispose() {

        if (timerAtualizacaoCards != null) {
            timerAtualizacaoCards.stop();
        }
        if (timerAtualizacaoGraficoBarra != null) {
            timerAtualizacaoGraficoBarra.stop();
        }
        if (timerAtualizacaoGraficoPizza != null) {
            timerAtualizacaoGraficoPizza.stop();
        }

        super.dispose();
    }

    // =========================
    // ATUALIZA TUDO
    // =========================
    public void atualizarDashboardCompleto() {
        atualizarCards();
        carregarGraficoBarra();
        carregarGraficoPizza();
    }

    // =========================
    // REDESENHAR
    // =========================
    private void redesenharGraficos() {
        if (chartBarra != null) {
            jpBarra.removeAll();
            jpBarra.add(criarChartPanel(chartBarra), BorderLayout.CENTER);
            jpBarra.revalidate();
            jpBarra.repaint();
        }

        if (chartPizza != null) {
            jpPizza.removeAll();
            jpPizza.add(criarChartPanel(chartPizza), BorderLayout.CENTER);
            jpPizza.revalidate();
            jpPizza.repaint();
        }
    }

    // =========================
    // TAMANHO PERSONALIZADO
    // =========================
    public void setTamanhoGrafico(int largura, int altura) {
        this.tamanhoGrafico = new Dimension(largura, altura);
        redesenharGraficos();
    }

    // =========================
    // ANIMAÇÃO DOS CARDS
    // =========================
    private void animarNumero(javax.swing.JLabel label, int valorFinal) {
        Timer timer = new Timer(15, null);
        final int[] valor = {0};

        timer.addActionListener(e -> {
            if (valor[0] < valorFinal) {
                valor[0]++;
                label.setText(String.valueOf(valor[0]));
            } else {
                label.setText(String.valueOf(valorFinal));
                timer.stop();
            }
        });
        timer.start();
    }

    private void animarMoeda(javax.swing.JLabel label, double valorFinal) {

        Timer timer = new Timer(15, null);
        final double[] valor = {0};

        timer.addActionListener(e -> {
            if (valor[0] < valorFinal) {
                valor[0] += Math.max(valorFinal / 50, 1);
                label.setText("R$ " + String.format("%.2f", valor[0]));
            } else {
                label.setText("R$ " + String.format("%.2f", valorFinal));
                timer.stop();
            }
        });

        timer.start();
    }

    /**
     * Método para atualizar a quantidade total de equipamentos.
     */
    public void atualizarTotalEquipamentos() {
        int total = Dashboard.getTotalEquipamentos();
        lblTotalEquipamentos.setText(" " + total);

        String quantidadePorTipo = Dashboard.getQuantidadePorTipo();
        txtQuantidadePorTipo.setText(quantidadePorTipo);

        double valorTotal = Dashboard.getValorTotalEquipamentos();
        lblCustoTotal.setText(" R$ " + String.format("%.2f", valorTotal));

        // == == == CARDS POR STATUS ======
        lblAtivos.setText(String.valueOf(Dashboard.getTotalPorStatus("Ativo")));

        //lblDevolvidos.setText(String.valueOf(Dashboard.getTotalPorStatus("Devolvido")));

        lblPendentes.setText(String.valueOf(Dashboard.getTotalPorStatus("Pendente")));

        //lblReservas.setText(String.valueOf(Dashboard.getTotalPorStatus("Reserva")));
    }

    private void atualizarCards() {

        try {
            animarNumero(lblTotalEquipamentos, Dashboard.getTotalEquipamentos());
            animarNumero(lblAtivos, Dashboard.getTotalPorStatus("Ativo"));
            animarNumero(lblDevolvidos, Dashboard.getTotalPorStatus("Devolvido"));
            animarNumero(lblPendentes, Dashboard.getTotalPorStatus("Pendente"));
            animarNumero(lblReservas, Dashboard.getTotalPorStatus("Reserva"));
            animarMoeda(lblCustoTotal, Dashboard.getValorTotalEquipamentos()
            );

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao atualizar cards: " + e.getMessage()
            );
        }
    }

    // =========================
    // GRÁFICO DE BARRAS (ANIMADO)
    // =========================
    private void carregarGraficoBarra() {

        DefaultCategoryDataset datasetFinal = new DefaultCategoryDataset();

        String sql = "SELECT tipo, COUNT(*) total FROM equipamentos GROUP BY tipo";

        try (PreparedStatement pst = conexao.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                datasetFinal.addValue(
                        rs.getInt("total"),
                        "Equipamentos",
                        rs.getString("tipo")
                );
            }

        } catch (Exception e) {
            System.out.println("Erro gráfico barra: " + e.getMessage());
        }

        animarGraficoBarra(datasetFinal);
    }

    private void animarGraficoBarra(DefaultCategoryDataset datasetFinal) {

        DefaultCategoryDataset datasetAnimado = new DefaultCategoryDataset();

        chartBarra = ChartFactory.createBarChart(
                "Equipamentos por Tipo",
                "Tipo",
                "Quantidade",
                datasetAnimado,
                PlotOrientation.VERTICAL,
                false, true, false
        );

        estilizarGraficoBarra(chartBarra);

        ChartPanel panel = criarChartPanel(chartBarra);
        jpBarra.removeAll();
        jpBarra.add(panel, BorderLayout.CENTER);

        Timer timer = new Timer(30, null);
        final int[] passo = {0};

        timer.addActionListener(e -> {
            datasetAnimado.clear();

            for (int i = 0; i < datasetFinal.getColumnCount(); i++) {
                int valor = datasetFinal.getValue(0, i).intValue();
                datasetAnimado.addValue(
                        Math.min(passo[0], valor),
                        "Equipamentos",
                        datasetFinal.getColumnKey(i)
                );
            }

            passo[0]++;
            if (passo[0] > 100) {
                timer.stop();
            }
        });

        timer.start();
    }

    private void estilizarGraficoBarra(JFreeChart chart) {
        chart.getTitle().setFont(new Font("Arial", Font.BOLD, 14));

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.GRAY);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(79, 129, 189));
        renderer.setItemMargin(0.15);
        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setBaseItemLabelsVisible(true);
        renderer.setBaseItemLabelFont(new Font("Arial", Font.BOLD, 10));
    }

    // =========================
    // GRÁFICO DE PIZZA (ANIMADO)
    // =========================
    private void carregarGraficoPizza() {

        DefaultPieDataset datasetFinal = new DefaultPieDataset();

        String sql = "SELECT IFNULL(status,'Não informado') status, COUNT(*) total FROM equipamentos GROUP BY status";

        try (PreparedStatement pst = conexao.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                datasetFinal.setValue(rs.getString("status"), rs.getInt("total"));
            }

        } catch (Exception e) {
            System.out.println("Erro gráfico pizza: " + e.getMessage());
        }

        animarGraficoPizza(datasetFinal);
    }

    private void animarGraficoPizza(DefaultPieDataset datasetFinal) {

        DefaultPieDataset datasetAnimado = new DefaultPieDataset();

        chartPizza = ChartFactory.createPieChart(
                "Equipamentos por Status",
                datasetAnimado,
                true, true, false
        );

        estilizarGraficoPizza(chartPizza);

        ChartPanel panel = criarChartPanel(chartPizza);
        jpPizza.removeAll();
        jpPizza.add(panel, BorderLayout.CENTER);

        Timer timer = new Timer(50, null);
        final int[] index = {0};

        timer.addActionListener(e -> {
            if (index[0] < datasetFinal.getItemCount()) {
                Comparable key = datasetFinal.getKey(index[0]);
                datasetAnimado.setValue(key, datasetFinal.getValue(key));
                index[0]++;
            } else {
                timer.stop();
            }
        });

        timer.start();
    }

    private void estilizarGraficoPizza(JFreeChart chart) {
        chart.getTitle().setFont(new Font("Arial", Font.BOLD, 14));
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setLabelFont(new Font("Arial", Font.PLAIN, 10));
        plot.setLabelGenerator(
                new StandardPieSectionLabelGenerator("{0}: {1} ({2})")
        );
    }

    // =========================
    // CHART PANEL PADRÃO
    // =========================
    private ChartPanel criarChartPanel(JFreeChart chart) {

        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(tamanhoGrafico);
        panel.setMinimumSize(new Dimension(300, 200));

        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                panel.setPreferredSize(panel.getSize());
            }
        });

        return panel;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        lblPendentes = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        cardPendentes = new javax.swing.JPanel();
        lblReservas = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel33 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        lblAtivos = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        lblCustoTotal = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        ValorTotalEquipamentos = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        lblDevolvidos = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        lblTotalEquipamentos = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        lblDesktop = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        lblAdaptador = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        lblMonitor = new javax.swing.JLabel();
        jpBarra = new javax.swing.JPanel();
        jpPizza = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtQuantidadePorTipo = new javax.swing.JTextField();

        setBackground(new java.awt.Color(255, 255, 255));
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Painel Dashboard");

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jPanel13.setBackground(new java.awt.Color(255, 255, 0));
        jPanel13.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel14.setBackground(new java.awt.Color(255, 255, 102));

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/megacenter/icones/Icon_Pendente.png"))); // NOI18N
        jLabel15.setText("Pendente");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel15)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel13.add(jPanel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 220, -1));

        lblPendentes.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lblPendentes.setText(":");
        jPanel13.add(lblPendentes, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 85, -1));

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel25.setText("Equipamentos pendentes");
        jPanel13.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, 170, -1));

        cardPendentes.setBackground(new java.awt.Color(255, 204, 0));
        cardPendentes.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblReservas.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lblReservas.setText(":");
        cardPendentes.add(lblReservas, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 91, -1));

        jLabel34.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel34.setText("Equipamentos reservas");
        cardPendentes.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, -1, -1));

        jPanel6.setBackground(new java.awt.Color(255, 204, 51));

        jLabel33.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel33.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/megacenter/icones/Icon_Reserva.png"))); // NOI18N
        jLabel33.setText("Reserva");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel33)
                .addContainerGap())
        );

        cardPendentes.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 220, -1));

        jPanel15.setBackground(new java.awt.Color(0, 204, 0));
        jPanel15.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblAtivos.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lblAtivos.setText(":");
        jPanel15.add(lblAtivos, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 62, 70, -1));

        jPanel16.setBackground(new java.awt.Color(0, 204, 102));

        jLabel35.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel35.setText("Ativos");

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel35)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel15.add(jPanel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 220, 49));

        jLabel36.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel36.setText("Equipamentos instalados");
        jPanel15.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, -1, -1));

        jPanel3.setBackground(new java.awt.Color(9, 132, 227));
        jPanel3.setPreferredSize(new java.awt.Dimension(134, 125));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblCustoTotal.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lblCustoTotal.setForeground(new java.awt.Color(255, 255, 255));
        lblCustoTotal.setText(":");
        jPanel3.add(lblCustoTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 61, 180, -1));

        jPanel12.setBackground(new java.awt.Color(116, 185, 255));

        ValorTotalEquipamentos.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        ValorTotalEquipamentos.setText("Custo");

        jLabel26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/megacenter/icones/despesas.png"))); // NOI18N

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel26)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ValorTotalEquipamentos, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(34, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ValorTotalEquipamentos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 12, Short.MAX_VALUE))
        );

        jPanel3.add(jPanel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 220, -1));

        jLabel28.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(255, 255, 255));
        jLabel28.setText("Despesas com equipamentos");
        jPanel3.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 108, 190, -1));

        jPanel8.setBackground(new java.awt.Color(255, 0, 0));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblDevolvidos.setBackground(new java.awt.Color(255, 255, 255));
        lblDevolvidos.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lblDevolvidos.setForeground(new java.awt.Color(255, 255, 255));
        lblDevolvidos.setText(":");
        jPanel8.add(lblDevolvidos, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 62, 150, -1));

        jPanel5.setBackground(new java.awt.Color(255, 51, 51));

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel14.setText("Devolvidos");

        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/megacenter/icones/troca.png"))); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel8.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 220, -1));

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(255, 255, 255));
        jLabel24.setText("Equipamentos devolvidos");
        jPanel8.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 109, 170, -1));

        jPanel4.setBackground(new java.awt.Color(0, 184, 148));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTotalEquipamentos.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lblTotalEquipamentos.setForeground(new java.awt.Color(255, 255, 255));
        lblTotalEquipamentos.setText(":");
        jPanel4.add(lblTotalEquipamentos, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 170, -1));

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setText("Equipamentos cadastrado");
        jPanel4.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 107, 180, -1));

        jPanel7.setBackground(new java.awt.Color(0, 204, 102));

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/megacenter/icones/Icon_Eletronico.png"))); // NOI18N
        jLabel12.setText("Todos");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(187, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jPanel4.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 300, 60));

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel13.setText("Painel Dashboard");

        lblDesktop.setText("Desktop");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblDesktop)
                .addContainerGap(210, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblDesktop)
                .addContainerGap(31, Short.MAX_VALUE))
        );

        lblAdaptador.setText("Monitor");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblAdaptador)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblAdaptador)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblMonitor.setText("Adaptador");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblMonitor)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblMonitor)
                .addContainerGap(36, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(12, 12, 12)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cardPendentes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE))
                                .addGap(18, 18, 18)))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cardPendentes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                            .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jpBarra.setPreferredSize(new java.awt.Dimension(349, 261));

        javax.swing.GroupLayout jpBarraLayout = new javax.swing.GroupLayout(jpBarra);
        jpBarra.setLayout(jpBarraLayout);
        jpBarraLayout.setHorizontalGroup(
            jpBarraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 349, Short.MAX_VALUE)
        );
        jpBarraLayout.setVerticalGroup(
            jpBarraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jpPizza.setPreferredSize(new java.awt.Dimension(349, 261));

        javax.swing.GroupLayout jpPizzaLayout = new javax.swing.GroupLayout(jpPizza);
        jpPizza.setLayout(jpPizzaLayout);
        jpPizzaLayout.setHorizontalGroup(
            jpPizzaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 349, Short.MAX_VALUE)
        );
        jpPizzaLayout.setVerticalGroup(
            jpPizzaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 341, Short.MAX_VALUE)
        );

        jPanel1.setBackground(new java.awt.Color(0, 51, 153));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("G&C");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Protection");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addContainerGap(632, Short.MAX_VALUE))
        );

        txtQuantidadePorTipo.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        txtQuantidadePorTipo.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtQuantidadePorTipo.setEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jpPizza, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jpBarra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(2, 2, 2)))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(txtQuantidadePorTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtQuantidadePorTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jpPizza, javax.swing.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE)
                            .addComponent(jpBarra, javax.swing.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE)))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel ValorTotalEquipamentos;
    private javax.swing.JPanel cardPendentes;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jpBarra;
    private javax.swing.JPanel jpPizza;
    private javax.swing.JLabel lblAdaptador;
    private javax.swing.JLabel lblAtivos;
    private javax.swing.JLabel lblCustoTotal;
    private javax.swing.JLabel lblDesktop;
    private javax.swing.JLabel lblDevolvidos;
    private javax.swing.JLabel lblMonitor;
    private javax.swing.JLabel lblPendentes;
    private javax.swing.JLabel lblReservas;
    private javax.swing.JLabel lblTotalEquipamentos;
    private javax.swing.JTextField txtQuantidadePorTipo;
    // End of variables declaration//GEN-END:variables
}
