package livrocaixa.view;

import livrocaixa.controller.LivroCaixaController;
import livrocaixa.model.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Camada View do protótipo visual Livro Caixa.
 *
 * <p>O projeto demonstra a navegação e o comportamento das telas em Java Swing.
 * Os dados usados na interface são amostras mantidas apenas em memória; não há
 * banco de dados nem persistência de cadastro nesta versão.</p>
 */
public final class LivroCaixaView {
    static final Color NAVY = new Color(5, 46, 78), BLUE = new Color(0, 91, 234), PALE = new Color(244, 248, 255);
    static final Color GREEN = new Color(18, 150, 73), RED = new Color(220, 38, 38), ORANGE = new Color(245, 145, 0);
    static final Font NORMAL = new Font("Segoe UI", Font.PLAIN, 17), BOLD = new Font("Segoe UI", Font.BOLD, 17);
    static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    static final NumberFormat BRL = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    /**
     * Configura a interface e abre a tela de acesso.
     * O Controller entrega o cenário demonstrativo que será exibido pela View.
     */
    public static void start(LivroCaixaController controller) {
        Objects.requireNonNull(controller, "controller");
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        UiKit.installDefaults();
        UIManager.put("Label.font", NORMAL); UIManager.put("Button.font", BOLD); UIManager.put("TextField.font", NORMAL);
        UIManager.put("PasswordField.font", NORMAL); UIManager.put("ComboBox.font", NORMAL); UIManager.put("Table.font", NORMAL); UIManager.put("TableHeader.font", BOLD);
        UIManager.put("TabbedPane.font", BOLD); UIManager.put("OptionPane.messageFont", NORMAL); UIManager.put("OptionPane.buttonFont", BOLD);
        new LoginFrame(controller).setVisible(true);
    }

    /** Janela inicial demonstrativa. A autenticação apenas valida campos preenchidos. */
    static final class LoginFrame extends JFrame {
        private final LivroCaixaController controller;

        LoginFrame(LivroCaixaController controller) {
            super("Livro Caixa");setIconImage(appIcon()); setDefaultCloseOperation(EXIT_ON_CLOSE); setSize(1200, 675); setMinimumSize(new Dimension(980,580)); setLocationRelativeTo(null); setResizable(true);
            this.controller = Objects.requireNonNull(controller, "controller");
            JPanel root = new JPanel(new BorderLayout()); root.setBackground(new Color(246,249,255));
            UiKit.GradientPanel brand = new UiKit.GradientPanel(new Color(3,39,68), new Color(9,75,115), false); brand.setPreferredSize(new Dimension(340, 0)); brand.setLayout(new GridBagLayout());
            JPanel brandContent = new JPanel(); brandContent.setOpaque(false); brandContent.setLayout(new BoxLayout(brandContent,BoxLayout.Y_AXIS));
            JLabel mark = new JLabel(UiKit.vectorIcon("wallet",66,Color.WHITE)); mark.setAlignmentX(.5f); brandContent.add(mark); brandContent.add(Box.createVerticalStrut(20));
            JLabel logo = new JLabel("Livro Caixa"); logo.setForeground(Color.WHITE); logo.setFont(new Font("Segoe UI", Font.BOLD, 32)); logo.setAlignmentX(.5f); brandContent.add(logo);
            JLabel brandSub = new JLabel("CONTROLE FINANCEIRO"); brandSub.setForeground(new Color(190,220,240)); brandSub.setFont(new Font("Segoe UI",Font.PLAIN,12)); brandSub.setAlignmentX(.5f); brandContent.add(Box.createVerticalStrut(9)); brandContent.add(brandSub); brand.add(brandContent);
            JPanel form = new JPanel(new GridBagLayout()); form.setOpaque(false); UiKit.RoundPanel card = new UiKit.RoundPanel(18).fill(Color.WHITE); card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS)); card.setBorder(new EmptyBorder(28,38,24,38)); card.setPreferredSize(new Dimension(500, 430));
            JLabel title = title("Acesso ao sistema", 30); title.setForeground(new Color(13,30,58)); title.setAlignmentX(.5f); card.add(title);
            JLabel sub = new JLabel("Informe seus dados para continuar"); sub.setAlignmentX(.5f); sub.setForeground(new Color(100,112,132)); card.add(Box.createVerticalStrut(4)); card.add(sub); card.add(Box.createVerticalStrut(26));
            JTextField user = modernField("Ana Souza"); JPasswordField pass = modernPassword("123456");
            JPanel userGroup=labeled("Nome do funcionário",user),passGroup=labeled("Senha",pass);Dimension loginFieldSize=new Dimension(424,76);userGroup.setPreferredSize(loginFieldSize);userGroup.setMinimumSize(loginFieldSize);userGroup.setMaximumSize(loginFieldSize);userGroup.setAlignmentX(.5f);passGroup.setPreferredSize(loginFieldSize);passGroup.setMinimumSize(loginFieldSize);passGroup.setMaximumSize(loginFieldSize);passGroup.setAlignmentX(.5f);card.add(userGroup);card.add(Box.createVerticalStrut(8));card.add(passGroup);
            JCheckBox show = new JCheckBox("Mostrar senha"); show.setOpaque(false); show.setForeground(new Color(70,82,105)); show.addActionListener(e -> pass.setEchoChar(show.isSelected() ? (char)0 : '•'));JPanel checkRow=new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));checkRow.setOpaque(false);checkRow.setPreferredSize(new Dimension(424,28));checkRow.setMinimumSize(new Dimension(424,28));checkRow.setMaximumSize(new Dimension(424,28));checkRow.setAlignmentX(.5f);checkRow.add(show);card.add(checkRow);
            JButton enter = new UiKit.RoundButton("Entrar",UiKit.vectorIcon("logout",22,Color.WHITE),BLUE,Color.WHITE); enter.setAlignmentX(.5f); enter.setMaximumSize(new Dimension(424, 50)); enter.setPreferredSize(new Dimension(424,50));
            enter.addActionListener(e -> { if (user.getText().isBlank() || pass.getPassword().length == 0) error(this,"Preencha usuário e senha."); else { dispose(); new MainFrame(controller, user.getText().trim()).setVisible(true); }});
            card.add(Box.createVerticalStrut(16)); card.add(enter); card.add(Box.createVerticalStrut(18)); JSeparator line=new JSeparator();line.setMaximumSize(new Dimension(Integer.MAX_VALUE,1));card.add(line);card.add(Box.createVerticalStrut(10));
            JLabel foot=new JLabel("Controle financeiro");foot.setForeground(new Color(135,145,160));foot.setFont(new Font("Segoe UI",Font.PLAIN,12));foot.setAlignmentX(.5f);card.add(foot);
            form.add(card); root.add(brand, BorderLayout.WEST); root.add(form); setContentPane(root); getRootPane().setDefaultButton(enter);
        }
    }

    /**
     * Moldura principal compartilhada por todas as telas.
     * Mantém o menu lateral e o cabeçalho fixos; somente {@code content} é trocado
     * quando o usuário navega pelo sistema.
     */
    static final class MainFrame extends JFrame {
        final LivroCaixaController controller;
        final LivroCaixaModel store;
        final String employee; final JPanel content = new JPanel(new BorderLayout()); final JLabel pageTitle = title("Painel", 30);
        final Map<String,JButton> nav = new LinkedHashMap<>();
        MainFrame(LivroCaixaController controller, String employee) {
            super("Livro Caixa");setIconImage(appIcon()); this.controller=Objects.requireNonNull(controller, "controller"); this.store=controller.getModel(); this.employee=employee; setDefaultCloseOperation(EXIT_ON_CLOSE); setMinimumSize(new Dimension(1180,700)); setSize(1500,850); setLocationRelativeTo(null);setExtendedState(JFrame.MAXIMIZED_BOTH);
            JPanel shell = new JPanel(new BorderLayout()); UiKit.GradientPanel side = new UiKit.GradientPanel(new Color(4,38,67),new Color(9,71,108),false); side.setPreferredSize(new Dimension(280,0)); side.setLayout(new BoxLayout(side,BoxLayout.Y_AXIS));
            JLabel logo = new JLabel("Livro Caixa"); logo.setIcon(UiKit.vectorIcon("wallet",38,Color.WHITE)); logo.setIconTextGap(13); logo.setForeground(Color.WHITE); logo.setFont(new Font("Segoe UI",Font.BOLD,31)); logo.setBorder(new EmptyBorder(26,24,36,10)); logo.setAlignmentX(LEFT_ALIGNMENT); side.add(logo);
            addNav(side,"Painel","home"); addNav(side,"Clientes","user"); addNav(side,"Consultas","calendar"); addNav(side,"Contas a receber","coins"); addNav(side,"Saídas","swap"); addNav(side,"Relatórios","chart"); addNav(side,"Configurações","gear");
            JPanel right = new JPanel(new BorderLayout()); UiKit.GradientPanel top = new UiKit.GradientPanel(new Color(8,104,215),new Color(0,74,215),true); top.setPreferredSize(new Dimension(0,84)); top.setLayout(new BorderLayout()); top.setBorder(new EmptyBorder(15,24,15,30)); pageTitle.setForeground(Color.WHITE);pageTitle.setFont(new Font("Segoe UI",Font.BOLD,32)); top.add(pageTitle,BorderLayout.WEST);
            JPanel whoPanel=new JPanel(new FlowLayout(FlowLayout.RIGHT,12,0));whoPanel.setOpaque(false);JLabel personIcon=new JLabel(UiKit.vectorIcon("user",28,Color.WHITE));JLabel who=new JLabel("Funcionário:  "+employee);who.setFont(new Font("Segoe UI",Font.PLAIN,17));who.setForeground(Color.WHITE);JLabel arrow=new JLabel(UiKit.vectorIcon("chevronDown",18,Color.WHITE));whoPanel.add(personIcon);whoPanel.add(who);whoPanel.add(arrow);top.add(whoPanel,BorderLayout.EAST);
            content.setBackground(PALE); right.add(top,BorderLayout.NORTH); right.add(content,BorderLayout.CENTER); shell.add(side,BorderLayout.WEST); shell.add(right); setContentPane(shell); showPage("Painel");
            addWindowListener(new WindowAdapter(){ public void windowClosing(WindowEvent e){ store.save(); }});
        }
        /** Registra uma opção no menu e direciona o clique para {@link #showPage(String)}. */
        void addNav(JPanel side,String name,String icon){ JButton b=new NavButton(name,UiKit.vectorIcon(icon,28,Color.WHITE)); b.setMaximumSize(new Dimension(258,58)); b.setPreferredSize(new Dimension(258,58)); b.setAlignmentX(LEFT_ALIGNMENT); b.addActionListener(e->showPage(name)); side.add(b); side.add(Box.createVerticalStrut(4)); nav.put(name,b); }

        /** Seleciona uma tela principal e destaca a opção correspondente no menu lateral. */
        void showPage(String name){ pageTitle.setText(name); content.removeAll(); nav.forEach((n,b)->{b.setBackground(n.equals(name)?new Color(0,94,245):new Color(0,0,0,0));b.repaint();}); JComponent p=switch(name){case "Clientes"->clients();case "Consultas"->appointments();case "Contas a receber"->receivables();case "Saídas"->expenses();case "Relatórios"->reports();case "Configurações"->settings();default->dashboard();}; content.add(p); content.revalidate(); content.repaint(); }

        /** Exibe formulários e etapas internas sem recriar a janela ou o menu principal. */
        void showCustom(String titleText,JComponent panel){pageTitle.setText(titleText);content.removeAll();nav.forEach((n,b)->{b.setBackground(new Color(0,0,0,0));b.repaint();});content.add(panel);content.revalidate();content.repaint();}
        void showSuccessPage(String message,String backPage){
            JPanel root=page();
            root.setLayout(new GridBagLayout());
            UiKit.RoundPanel box=new UiKit.RoundPanel(20).fill(Color.WHITE);
            box.setPreferredSize(new Dimension(520,330));
            box.setLayout(new BoxLayout(box,BoxLayout.Y_AXIS));
            box.setBorder(new EmptyBorder(30,45,30,45));

            Color success=new Color(8,139,38);
            UiKit.RoundPanel iconBox=new UiKit.RoundPanel(22).fill(new Color(218,247,224)).line(null).shadow(false);
            iconBox.setLayout(new GridBagLayout());
            iconBox.setAlignmentX(.5f);
            iconBox.setPreferredSize(new Dimension(86,86));
            iconBox.setMaximumSize(new Dimension(86,86));
            iconBox.add(new JLabel(UiKit.vectorIcon("coins",46,success)));
            box.add(iconBox);
            box.add(Box.createVerticalStrut(18));

            JLabel heading=title(message,26);
            heading.setAlignmentX(.5f);
            box.add(heading);
            box.add(Box.createVerticalStrut(8));
            JLabel sub=new JLabel("Os dados foram registrados com sucesso.");
            sub.setForeground(UiKit.MUTED);
            sub.setAlignmentX(.5f);
            box.add(sub);
            box.add(Box.createVerticalStrut(26));
            JButton ok=primary("OK");
            ok.setAlignmentX(.5f);
            ok.setMaximumSize(new Dimension(240,48));
            ok.addActionListener(e->showPage(backPage));
            box.add(ok);
            root.add(box);
            showCustom("Confirmação",root);
        }

        JComponent dashboard(){ JPanel p=page(); JPanel metrics=new JPanel(new GridLayout(1,4,14,0)); metrics.setOpaque(false); metrics.setPreferredSize(new Dimension(0,142)); metrics.add(metric("Saldo atual",money(store.balance()),BLUE,"wallet")); metrics.add(metric("Entradas no mês",money(store.income()),new Color(0,177,183),"arrowUp")); metrics.add(metric("Saídas no mês",money(store.expenseTotal()),new Color(230,35,43),"arrowDown")); metrics.add(metric("A receber",money(store.pending()),ORANGE,"coins")); p.add(metrics,BorderLayout.NORTH);
            JPanel center=new JPanel(new GridLayout(1,2,14,0)); center.setOpaque(false); center.setBorder(new EmptyBorder(16,0,16,0)); UiKit.RoundPanel chartCard=new UiKit.RoundPanel(16).fill(Color.WHITE);chartCard.setLayout(new BorderLayout());chartCard.setBorder(new EmptyBorder(16,18,14,18));chartCard.add(title("Fluxo de caixa",23),BorderLayout.NORTH);chartCard.add(cashFlowChart(store.income(),store.expenseTotal()));center.add(chartCard);
            UiKit.RoundPanel dueCard=new UiKit.RoundPanel(16).fill(Color.WHITE);dueCard.setLayout(new BorderLayout());dueCard.setBorder(new EmptyBorder(16,12,16,12));JLabel dueTitle=title("Próximos vencimentos",23);dueTitle.setBorder(new EmptyBorder(0,4,12,4));dueCard.add(dueTitle,BorderLayout.NORTH);JTable dueTable=styledTable(new DefaultTableModel(store.upcomingRows(),new String[]{"Cliente","Parcela","Vencimento","Valor","Status"}){public boolean isCellEditable(int r,int c){return false;}});dueTable.setRowHeight(38);dueCard.add(new JScrollPane(dueTable));center.add(dueCard); p.add(center);
            JPanel quick=new JPanel(new GridLayout(1,4,12,0)); quick.setOpaque(false);quick.setPreferredSize(new Dimension(0,62)); quick.add(quickButton("Novo cliente","user",e->clientDialog(null))); quick.add(quickButton("Nova consulta","calendar",e->appointmentDialog(null))); quick.add(quickButton("Registrar pagamento","coins",e->showPage("Contas a receber"))); quick.add(quickButton("Nova saída","logout",e->expenseDialog(null))); p.add(quick,BorderLayout.SOUTH); return p; }

        JComponent clients(){return OperationalListScreens.buildClients(this);}
        void clientDialog(Client edit){
            JPanel root=page();
            UiKit.RoundPanel card=new UiKit.RoundPanel(16).fill(Color.WHITE);
            card.setLayout(new BorderLayout(0,22));
            card.setBorder(new EmptyBorder(24,26,22,26));

            JPanel heading=new JPanel();
            heading.setOpaque(false);
            heading.setLayout(new BoxLayout(heading,BoxLayout.Y_AXIS));
            heading.add(title(edit==null?"Novo cliente":"Editar cliente",32));
            JLabel subtitle=new JLabel(edit==null?"Preencha os dados para cadastrar um novo cliente.":"Atualize os dados cadastrais do cliente.");
            subtitle.setFont(new Font("Segoe UI",Font.PLAIN,18));
            subtitle.setForeground(UiKit.MUTED);
            heading.add(Box.createVerticalStrut(5));
            heading.add(subtitle);
            card.add(heading,BorderLayout.NORTH);

            JTextField name=modernField(edit==null?"":edit.name);
            JTextField cpf=modernField(edit==null?"":edit.cpf);
            name.setFont(new Font("Segoe UI",Font.PLAIN,19));
            cpf.setFont(new Font("Segoe UI",Font.PLAIN,19));

            UiKit.RoundPanel formCard=new UiKit.RoundPanel(14).fill(new Color(248,250,255)).shadow(false);
            formCard.setLayout(new BoxLayout(formCard,BoxLayout.Y_AXIS));
            formCard.setBorder(new EmptyBorder(22,24,22,24));
            formCard.add(taggedField("Nome completo","Obrigatório",name));
            formCard.add(Box.createVerticalStrut(17));
            formCard.add(taggedField("CPF","Obrigatório",cpf));
            JLabel help=new JLabel("Use o formato 000.000.000-00");
            help.setIcon(UiKit.vectorIcon("info",16,UiKit.MUTED));
            help.setIconTextGap(8);
            help.setForeground(UiKit.MUTED);
            help.setFont(new Font("Segoe UI",Font.PLAIN,15));
            help.setAlignmentX(Component.LEFT_ALIGNMENT);
            formCard.add(Box.createVerticalStrut(8));
            formCard.add(help);

            UiKit.RoundPanel summary=new UiKit.RoundPanel(14).fill(new Color(244,248,255)).shadow(false);
            summary.setLayout(new BoxLayout(summary,BoxLayout.Y_AXIS));
            summary.setBorder(new EmptyBorder(20,22,20,22));
            JPanel summaryHeading=new JPanel(new BorderLayout(10,0));
            summaryHeading.setOpaque(false);
            JLabel summaryIcon=new JLabel(UiKit.vectorIcon("user",24,BLUE));
            summaryHeading.add(summaryIcon,BorderLayout.WEST);
            JPanel summaryText=new JPanel();
            summaryText.setOpaque(false);
            summaryText.setLayout(new BoxLayout(summaryText,BoxLayout.Y_AXIS));
            summaryText.add(title("Pré-visualização",22));
            JLabel summaryHint=new JLabel("Confira os dados antes de salvar");
            summaryHint.setFont(new Font("Segoe UI",Font.PLAIN,14));
            summaryHint.setForeground(UiKit.MUTED);
            summaryText.add(summaryHint);
            summaryHeading.add(summaryText);
            summaryHeading.setAlignmentX(Component.LEFT_ALIGNMENT);
            summaryHeading.setMaximumSize(new Dimension(Integer.MAX_VALUE,52));
            summary.add(summaryHeading);
            summary.add(Box.createVerticalStrut(16));
            JLabel summaryName=new JLabel(name.getText().isBlank()?"Ainda não informado":name.getText().trim());
            JLabel summaryCpf=new JLabel(cpf.getText().isBlank()?"Ainda não informado":cpf.getText().trim());
            summary.add(clientPreviewItem("Nome",summaryName,"user"));
            summary.add(Box.createVerticalStrut(10));
            summary.add(clientPreviewItem("CPF",summaryCpf,"document"));

            DocumentListener previewListener=new DocumentListener(){
                private void update(){
                    String nameValue=name.getText().trim(),cpfValue=cpf.getText().trim();
                    summaryName.setText(nameValue.isBlank()?"Ainda não informado":nameValue);
                    summaryCpf.setText(cpfValue.isBlank()?"Ainda não informado":cpfValue);
                }
                @Override public void insertUpdate(DocumentEvent e){update();}
                @Override public void removeUpdate(DocumentEvent e){update();}
                @Override public void changedUpdate(DocumentEvent e){update();}
            };
            name.getDocument().addDocumentListener(previewListener);
            cpf.getDocument().addDocumentListener(previewListener);

            JPanel body=new JPanel(new GridBagLayout());
            body.setOpaque(false);
            GridBagConstraints gc=new GridBagConstraints();
            gc.gridy=0;
            gc.fill=GridBagConstraints.HORIZONTAL;
            gc.anchor=GridBagConstraints.NORTH;
            gc.weighty=0;
            gc.gridx=0;
            gc.weightx=1.8;
            gc.insets=new Insets(0,0,0,20);
            body.add(formCard,gc);
            gc.gridx=1;
            gc.weightx=1;
            gc.insets=new Insets(0,0,0,0);
            body.add(summary,gc);
            JPanel bodyHolder=new JPanel(new BorderLayout());
            bodyHolder.setOpaque(false);
            bodyHolder.add(body,BorderLayout.NORTH);
            card.add(bodyHolder);

            JPanel actions=new JPanel(new FlowLayout(FlowLayout.RIGHT,12,0));
            actions.setOpaque(false);
            actions.setBorder(new CompoundBorder(new MatteBorder(1,0,0,0,UiKit.LINE),new EmptyBorder(16,0,0,0)));
            JButton cancel=new UiKit.RoundButton("Cancelar",UiKit.vectorIcon("close",19,UiKit.INK),new Color(234,240,249),UiKit.INK);
            JButton save=new UiKit.RoundButton("Salvar cliente",UiKit.vectorIcon("save",20,Color.WHITE),BLUE,Color.WHITE);
            cancel.setPreferredSize(new Dimension(150,50));
            save.setPreferredSize(new Dimension(190,50));
            cancel.addActionListener(e->showPage("Clientes"));
            save.addActionListener(e->{if(name.getText().isBlank()||cpf.getText().isBlank()){error(this,"Informe o nome e o CPF.");return;}if(edit==null)store.clients.add(new Client(store.nextClient++,name.getText().trim(),cpf.getText().trim(),"","",""));else{edit.name=name.getText().trim();edit.cpf=cpf.getText().trim();}store.save();showPage("Clientes");});
            actions.add(cancel);
            actions.add(save);
            card.add(actions,BorderLayout.SOUTH);
            root.add(card,BorderLayout.NORTH);
            showCustom("Clientes",root);
        }

        JComponent appointments(){return OperationalListScreens.buildAppointments(this);}
        void appointmentDialog(Appointment edit){ConsultationScreens.showForm(this,edit);}

        void billingDialog(Appointment a){BillingScreen.show(this,a);}

        JComponent receivables(){return FinanceListScreens.buildReceivables(this);}
        void paymentDialog(Installment x){PaymentScreens.showPayment(this,x);}
        void paymentConfirmed(Installment x){ConfirmedPaymentScreen.show(this,x);}

        JComponent expenses(){return FinanceListScreens.buildExpenses(this);}
        void expenseDialog(Expense edit){
            JPanel root=page();UiKit.RoundPanel card=new UiKit.RoundPanel(16).fill(Color.WHITE);card.setLayout(new BorderLayout(18,18));card.setBorder(new EmptyBorder(18,22,18,22));JPanel heading=new JPanel();heading.setOpaque(false);heading.setLayout(new BoxLayout(heading,BoxLayout.Y_AXIS));heading.add(title(edit==null?"Nova saída":"Editar saída",34));JLabel sub=new JLabel("Registre uma despesa no livro caixa");sub.setFont(new Font("Segoe UI",Font.PLAIN,20));heading.add(sub);card.add(heading,BorderLayout.NORTH);
            JPanel body=new JPanel(new GridBagLayout());body.setOpaque(false);GridBagConstraints gc=new GridBagConstraints();gc.gridy=0;gc.fill=GridBagConstraints.BOTH;gc.weighty=1;gc.gridx=0;gc.weightx=2.2;gc.insets=new Insets(0,0,0,24);JPanel formArea=new JPanel();formArea.setOpaque(false);formArea.setLayout(new BoxLayout(formArea,BoxLayout.Y_AXIS));JComboBox<String> type=new JComboBox<>(store.expenseTypes.toArray(String[]::new));JTextField value=modernField(edit==null?"1.200,00":edit.value.toString()),date=modernField(fmt(edit==null?LocalDate.of(2026,11,5):edit.date));JComboBox<String> worker=new JComboBox<>(new String[]{employee,"Bruno Reis"});JTextArea notes=new JTextArea(edit==null?"Pagamento do aluguel do consultório.":edit.notes,4,30);notes.setLineWrap(true);notes.setBorder(new EmptyBorder(8,8,8,8));if(edit!=null){type.setSelectedItem(edit.type);worker.setSelectedItem(edit.employee);}JPanel typeLine=new JPanel(new BorderLayout(10,0));typeLine.setOpaque(false);typeLine.add(type);JButton newType=secondary("Novo tipo de saída");newType.setIcon(UiKit.vectorIcon("plus",18,BLUE));newType.addActionListener(e->{String s=JOptionPane.showInputDialog(this,"Descrição do tipo de saída:");if(s!=null&&!s.isBlank()){store.expenseTypes.add(s.trim());store.save();expenseDialog(edit);}});typeLine.add(newType,BorderLayout.EAST);formArea.add(taggedField("Tipo de saída","Obrigatório",typeLine));JLabel help=new JLabel("Cadastre um novo tipo sem sair desta tela.");help.setForeground(UiKit.MUTED);formArea.add(help);formArea.add(Box.createVerticalStrut(8));formArea.add(taggedField("Valor","Obrigatório",value));formArea.add(Box.createVerticalStrut(8));formArea.add(taggedField("Data","Obrigatório",date));formArea.add(Box.createVerticalStrut(8));formArea.add(taggedField("Funcionário","Obrigatório",worker));formArea.add(Box.createVerticalStrut(8));formArea.add(taggedField("Observações","Opcional",new JScrollPane(notes)));body.add(formArea,gc);
            gc.gridx=1;gc.weightx=1;gc.insets=new Insets(0,0,0,0);UiKit.RoundPanel summary=new UiKit.RoundPanel(14).fill(new Color(245,248,255)).shadow(false);summary.setLayout(new BoxLayout(summary,BoxLayout.Y_AXIS));summary.setBorder(new EmptyBorder(20,22,20,22));summary.add(title("Resumo da saída",24));summary.add(Box.createVerticalStrut(26));summary.add(title("Tipo de saída:",16));summary.add(new JLabel(String.valueOf(type.getSelectedItem())));summary.add(new JSeparator());summary.add(Box.createVerticalStrut(17));summary.add(title("Valor:",16));JLabel summaryValue=title(value.getText().isBlank()?"R$ 0,00":"R$ "+value.getText(),20);summaryValue.setForeground(BLUE);summary.add(summaryValue);summary.add(new JSeparator());summary.add(Box.createVerticalStrut(17));summary.add(title("Data:",16));summary.add(new JLabel(date.getText()));summary.add(new JSeparator());summary.add(Box.createVerticalStrut(17));summary.add(title("Funcionário:",16));summary.add(new JLabel(String.valueOf(worker.getSelectedItem())));body.add(summary,gc);card.add(body);
            JPanel actions=new JPanel(new FlowLayout(FlowLayout.RIGHT,14,0));actions.setOpaque(false);JButton cancel=new UiKit.RoundButton("Cancelar",UiKit.vectorIcon("close",20,UiKit.INK),new Color(234,240,249),UiKit.INK),save=new UiKit.RoundButton("Salvar saída",UiKit.vectorIcon("save",21,Color.WHITE),BLUE,Color.WHITE);cancel.addActionListener(e->showPage("Saídas"));save.addActionListener(e->{try{BigDecimal amount=parseMoney(value.getText());if(amount.signum()<=0)throw new IllegalArgumentException();String description=(String)type.getSelectedItem();if(edit==null)store.expenses.add(new Expense(store.nextExpense++,(String)type.getSelectedItem(),description,amount,parseDate(date.getText()),(String)worker.getSelectedItem(),notes.getText()));else{edit.type=(String)type.getSelectedItem();edit.description=description;edit.value=amount;edit.date=parseDate(date.getText());edit.employee=(String)worker.getSelectedItem();edit.notes=notes.getText();}store.save();showPage("Saídas");}catch(Exception ex){error(this,"Revise o valor e a data.");}});actions.add(cancel);actions.add(save);card.add(actions,BorderLayout.SOUTH);root.add(card);showCustom("Saídas",root);
        }

        JComponent reports(){return ManagementScreens.buildReports(this);}

        JComponent settings(){return ManagementScreens.buildSettings(this);}
    }

    static JPanel page(){JPanel p=new JPanel(new BorderLayout(0,12));p.setBackground(UiKit.BG);p.setBorder(new EmptyBorder(18,18,18,18));return p;}static JPanel form(){JPanel p=new JPanel();p.setBackground(UiKit.BG);p.setBorder(new EmptyBorder(22,22,22,22));p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));return p;}static JPanel card(){UiKit.RoundPanel p=new UiKit.RoundPanel(16).fill(Color.WHITE);p.setBorder(new EmptyBorder(12,18,12,18));return p;}
    static JLabel title(String s,int z){JLabel l=new JLabel(s);l.setFont(new Font("Segoe UI",Font.BOLD,z));l.setForeground(UiKit.INK);return l;}static JTextField field(String s){return modernField(s);}static JTextField modernField(String s){JTextField f=new JTextField(s);f.setFont(NORMAL);f.setMaximumSize(new Dimension(Integer.MAX_VALUE,46));f.setPreferredSize(new Dimension(250,46));f.setBackground(Color.WHITE);f.setForeground(UiKit.INK);f.setCaretColor(BLUE);f.setBorder(new CompoundBorder(new LineBorder(new Color(163,181,211)),new EmptyBorder(8,10,8,10)));return f;}static JPasswordField modernPassword(String s){JPasswordField f=new JPasswordField(s);f.setFont(NORMAL);f.setMaximumSize(new Dimension(Integer.MAX_VALUE,46));f.setPreferredSize(new Dimension(250,46));f.setBackground(Color.WHITE);f.setBorder(new CompoundBorder(new LineBorder(new Color(163,181,211)),new EmptyBorder(8,10,8,10)));return f;}static JPanel labeled(String name,Component c){JPanel p=new JPanel(new BorderLayout(0,5));p.setOpaque(false);p.setAlignmentX(Component.LEFT_ALIGNMENT);p.setMaximumSize(new Dimension(Integer.MAX_VALUE,c instanceof JScrollPane?135:82));JLabel l=new JLabel(name);l.setFont(BOLD);l.setForeground(UiKit.INK);p.add(l,BorderLayout.NORTH);p.add(c);return p;}
    static JPanel taggedField(String name,String tag,Component component){JPanel p=new JPanel(new BorderLayout(0,8));p.setOpaque(false);p.setAlignmentX(Component.LEFT_ALIGNMENT);JPanel head=new JPanel(new FlowLayout(FlowLayout.LEFT,10,0));head.setOpaque(false);JLabel label=title(name,19);JLabel badge=new JLabel(tag);badge.setFont(new Font("Segoe UI",Font.BOLD,13));boolean required="Obrigatório".equalsIgnoreCase(tag);badge.setForeground(required?new Color(190,25,30):new Color(70,76,88));badge.setOpaque(true);badge.setBackground(required?new Color(255,220,220):new Color(225,229,236));badge.setBorder(new CompoundBorder(new LineBorder(required?new Color(255,145,145):new Color(185,190,200)),new EmptyBorder(3,10,3,10)));head.add(label);head.add(badge);p.add(head,BorderLayout.NORTH);p.add(component);p.setMaximumSize(new Dimension(Integer.MAX_VALUE,92));return p;}
    static JComponent clientPreviewItem(String label,JLabel value,String icon){UiKit.RoundPanel row=new UiKit.RoundPanel(12).fill(Color.WHITE).line(UiKit.LINE).shadow(false);row.setLayout(new BorderLayout(12,0));row.setBorder(new EmptyBorder(11,12,11,12));row.setAlignmentX(Component.LEFT_ALIGNMENT);row.setMaximumSize(new Dimension(Integer.MAX_VALUE,68));UiKit.RoundPanel iconBox=new UiKit.RoundPanel(10).fill(new Color(231,240,255)).line(null).shadow(false);iconBox.setPreferredSize(new Dimension(44,44));iconBox.setLayout(new GridBagLayout());iconBox.add(new JLabel(UiKit.vectorIcon(icon,21,BLUE)));row.add(iconBox,BorderLayout.WEST);JPanel text=new JPanel();text.setOpaque(false);text.setLayout(new BoxLayout(text,BoxLayout.Y_AXIS));JLabel caption=new JLabel(label);caption.setFont(new Font("Segoe UI",Font.BOLD,13));caption.setForeground(UiKit.MUTED);value.setFont(new Font("Segoe UI",Font.BOLD,16));value.setForeground(UiKit.INK);text.add(caption);text.add(Box.createVerticalStrut(3));text.add(value);row.add(text);return row;}
    static JButton primary(String s){return new UiKit.RoundButton(s,null,BLUE,Color.WHITE);}static JButton secondary(String s){return new UiKit.RoundButton(s,null,new Color(235,241,250),UiKit.INK);}static JButton danger(String s){return new UiKit.RoundButton(s,UiKit.vectorIcon("trash",19,RED),new Color(255,238,240),RED);}static JButton quickButton(String s,ActionListener a){return quickButton(s,"plus",a);}static JButton quickButton(String s,String icon,ActionListener a){JButton b=new UiKit.RoundButton(s,UiKit.vectorIcon(icon,26,Color.WHITE),BLUE,Color.WHITE);b.setFont(new Font("Segoe UI",Font.BOLD,17));b.addActionListener(a);return b;}
    static JPanel metric(String name,String value,Color c){return metric(name,value,c,"wallet");}static JPanel metric(String name,String value,Color c,String icon){UiKit.RoundPanel p=new UiKit.RoundPanel(16).fill(Color.WHITE);p.setLayout(new BorderLayout(14,0));p.setBorder(new EmptyBorder(16,16,16,16));UiKit.RoundPanel iconBox=new UiKit.RoundPanel(13).fill(new Color(c.getRed(),c.getGreen(),c.getBlue(),28)).line(null).shadow(false);iconBox.setPreferredSize(new Dimension(62,62));iconBox.setLayout(new GridBagLayout());iconBox.add(new JLabel(UiKit.vectorIcon(icon,34,c)));p.add(iconBox,BorderLayout.WEST);JPanel text=new JPanel();text.setOpaque(false);text.setLayout(new BoxLayout(text,BoxLayout.Y_AXIS));JLabel n=title(name,17),v=title(value,27);v.setForeground(c);text.add(Box.createVerticalGlue());text.add(n);text.add(Box.createVerticalStrut(8));text.add(v);text.add(Box.createVerticalGlue());p.add(text);return p;}static Border titled(String s){return new TitledBorder(new LineBorder(new Color(185,205,235)),s,TitledBorder.LEADING,TitledBorder.TOP,new Font("Segoe UI",Font.BOLD,19),UiKit.INK);}
    static JTable styledTable(DefaultTableModel m){JTable t=UiKit.modernTable(m);t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);return t;}static JPanel tablePanel(String title,String[] cols,Object[][] rows){UiKit.RoundPanel p=new UiKit.RoundPanel(16).fill(Color.WHITE);p.setLayout(new BorderLayout());p.setBorder(new EmptyBorder(14,14,14,14));JLabel h=LivroCaixaView.title(title,21);h.setBorder(new EmptyBorder(0,2,12,2));p.add(h,BorderLayout.NORTH);JScrollPane scroll=new JScrollPane(styledTable(new DefaultTableModel(rows,cols){public boolean isCellEditable(int r,int c){return false;}}));scroll.setBorder(new LineBorder(UiKit.LINE));p.add(scroll);return p;}static JTable findTable(Container c){for(Component x:c.getComponents()){if(x instanceof JTable t)return t;if(x instanceof JScrollPane s&&s.getViewport().getView() instanceof JTable t)return t;if(x instanceof Container cc){JTable t=findTable(cc);if(t!=null)return t;}}return null;}

    static UiKit.CashFlowChart cashFlowChart(BigDecimal income,BigDecimal expense){double in=income.max(BigDecimal.ONE).doubleValue(),out=expense.max(BigDecimal.ONE).doubleValue();double[] ins={in*.63,in*.71,in*.79,in*.90,in*.98,in};double[] outs={out*.80,out*.96,out, out*.89,out*1.02,out};return new UiKit.CashFlowChart().values(new String[]{"Jun","Jul","Ago","Set","Out","Nov"},ins,outs);}
    static class NavButton extends JButton {boolean hover;NavButton(String text,Icon icon){super(text,icon);setHorizontalAlignment(LEFT);setIconTextGap(16);setForeground(Color.WHITE);setFont(new Font("Segoe UI",Font.BOLD,19));setOpaque(false);setContentAreaFilled(false);setBorderPainted(false);setFocusPainted(false);setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));setBorder(new EmptyBorder(12,24,12,12));addMouseListener(new MouseAdapter(){public void mouseEntered(MouseEvent e){hover=true;repaint();}public void mouseExited(MouseEvent e){hover=false;repaint();}});}protected void paintComponent(Graphics graphics){Graphics2D g=(Graphics2D)graphics.create();UiKit.antialias(g);Color bg=getBackground();if(bg.getAlpha()>0)g.setColor(bg);else g.setColor(hover?new Color(255,255,255,24):new Color(0,0,0,0));g.fillRoundRect(10,2,getWidth()-20,getHeight()-4,13,13);g.dispose();super.paintComponent(graphics);}}
    static Image appIcon(){BufferedImage image=new BufferedImage(64,64,BufferedImage.TYPE_INT_ARGB);Graphics2D g=image.createGraphics();UiKit.antialias(g);g.setColor(BLUE);g.fillRoundRect(3,3,58,58,15,15);Icon wallet=UiKit.vectorIcon("wallet",38,Color.WHITE);wallet.paintIcon(null,g,13,13);g.dispose();return image;}
    static JDialog dialog(Frame owner,String title,int w,int h){JDialog d=new JDialog(owner,title,true);d.setSize(w,h);d.setLocationRelativeTo(owner);return d;}
    static String fmt(LocalDate d){return d.format(DATE);}
    static LocalDate parseDate(String s){return LocalDate.parse(s.trim(),DATE);}
    static String money(BigDecimal v){return BRL.format(v);}
    static BigDecimal parseMoney(String s){String x=s.replace("R$","").replace("\u00A0","").replace("\u202F","").replaceAll("\\s+","").trim();if(x.contains(","))x=x.replace(".","").replace(",",".");return new BigDecimal(x).setScale(2,RoundingMode.HALF_UP);}
    static void info(Component c,String s){JOptionPane.showMessageDialog(c,s,"Livro Caixa",JOptionPane.INFORMATION_MESSAGE);}
    static void error(Component c,String s){JOptionPane.showMessageDialog(c,s,"Atenção",JOptionPane.ERROR_MESSAGE);}

    static boolean confirm(Component ownerComponent,String message){
        String normalized=message.toLowerCase(Locale.ROOT);
        String dialogTitle="Confirmar ação",actionLabel="Confirmar",detail="Revise os dados antes de continuar.",icon="warning";
        if(normalized.startsWith("excluir")){
            dialogTitle="Confirmar exclusão";
            actionLabel=normalized.contains("cliente")?"Excluir cliente":normalized.contains("saída")?"Excluir saída":normalized.contains("funcionário")?"Excluir funcionário":normalized.contains("cadastro")?"Excluir cadastro":"Excluir";
            detail="O registro será removido e esta ação não poderá ser desfeita.";
            icon="trash";
        }else if(normalized.startsWith("cancelar")){
            dialogTitle="Confirmar cancelamento";
            actionLabel="Cancelar parcela";
            detail="A parcela será marcada como cancelada. Confira antes de continuar.";
        }else if(normalized.startsWith("desfazer")){
            dialogTitle="Confirmar reversão";
            actionLabel="Desfazer baixa";
            detail="O pagamento e o desconto associados serão revertidos.";
            icon="refresh";
        }

        Window owner=ownerComponent instanceof Window window?window:SwingUtilities.getWindowAncestor(ownerComponent);
        JDialog confirmDialog=new JDialog(owner,Dialog.ModalityType.APPLICATION_MODAL);
        confirmDialog.setUndecorated(true);
        confirmDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        confirmDialog.setResizable(false);

        boolean[] accepted={false};
        UiKit.RoundPanel root=new UiKit.RoundPanel(18).fill(Color.WHITE).line(new Color(190,204,226)).shadow(true);
        root.setLayout(new BorderLayout(0,18));
        root.setBorder(new EmptyBorder(22,24,20,24));
        root.setPreferredSize(new Dimension(540,275));

        JPanel header=new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(title(dialogTitle,22),BorderLayout.WEST);
        JButton close=new JButton(UiKit.vectorIcon("close",18,UiKit.MUTED));
        close.setBorder(new EmptyBorder(5,8,5,8));
        close.setContentAreaFilled(false);
        close.setFocusPainted(false);
        close.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        close.addActionListener(e->confirmDialog.dispose());
        header.add(close,BorderLayout.EAST);
        root.add(header,BorderLayout.NORTH);

        JPanel content=new JPanel(new BorderLayout(18,0));
        content.setOpaque(false);
        UiKit.RoundPanel iconBox=new UiKit.RoundPanel(14).fill(new Color(255,233,235)).line(new Color(255,193,198)).shadow(false);
        iconBox.setPreferredSize(new Dimension(68,68));
        iconBox.setLayout(new GridBagLayout());
        iconBox.add(new JLabel(UiKit.vectorIcon(icon,32,UiKit.RED)));
        content.add(iconBox,BorderLayout.WEST);
        JPanel copy=new JPanel();
        copy.setOpaque(false);
        copy.setLayout(new BoxLayout(copy,BoxLayout.Y_AXIS));
        JTextArea question=new JTextArea(message);
        question.setEditable(false);
        question.setFocusable(false);
        question.setOpaque(false);
        question.setLineWrap(true);
        question.setWrapStyleWord(true);
        question.setRows(2);
        question.setColumns(29);
        question.setFont(new Font("Segoe UI",Font.BOLD,18));
        question.setForeground(UiKit.INK);
        question.setBorder(null);
        JTextArea explanation=new JTextArea(detail);
        explanation.setEditable(false);
        explanation.setFocusable(false);
        explanation.setOpaque(false);
        explanation.setLineWrap(true);
        explanation.setWrapStyleWord(true);
        explanation.setRows(2);
        explanation.setColumns(29);
        explanation.setFont(new Font("Segoe UI",Font.PLAIN,15));
        explanation.setForeground(UiKit.MUTED);
        explanation.setBorder(null);
        copy.add(question);
        copy.add(Box.createVerticalStrut(5));
        copy.add(explanation);
        content.add(copy);
        root.add(content);

        JPanel actions=new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0));
        actions.setOpaque(false);
        actions.setBorder(new CompoundBorder(new MatteBorder(1,0,0,0,UiKit.LINE),new EmptyBorder(15,0,0,0)));
        JButton back=new UiKit.RoundButton("Voltar",UiKit.vectorIcon("close",18,UiKit.INK),new Color(235,241,250),UiKit.INK);
        JButton proceed=new UiKit.RoundButton(actionLabel,UiKit.vectorIcon(icon,18,Color.WHITE),UiKit.RED,Color.WHITE);
        back.setPreferredSize(new Dimension(140,48));
        proceed.setPreferredSize(new Dimension(190,48));
        back.addActionListener(e->confirmDialog.dispose());
        proceed.addActionListener(e->{accepted[0]=true;confirmDialog.dispose();});
        actions.add(back);
        actions.add(proceed);
        root.add(actions,BorderLayout.SOUTH);

        confirmDialog.setContentPane(root);
        confirmDialog.getRootPane().setDefaultButton(back);
        confirmDialog.getRootPane().registerKeyboardAction(e->confirmDialog.dispose(),KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0),JComponent.WHEN_IN_FOCUSED_WINDOW);
        confirmDialog.pack();
        confirmDialog.setLocationRelativeTo(ownerComponent);
        confirmDialog.setVisible(true);
        return accepted[0];
    }
}
