package livrocaixa.view;

import livrocaixa.model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

/** Fluxo de registro de pagamentos e descontos das parcelas. */
final class PaymentScreens {
    private PaymentScreens() {}

    static void showPayment(LivroCaixaView.MainFrame frame, Installment item) {
        if (item.cancelled) { LivroCaixaView.error(frame, "Parcela cancelada."); return; }
        if (item.paid.compareTo(item.value) >= 0) { frame.paymentConfirmed(item); return; }
        BigDecimal originalBalance = item.value.subtract(item.paid);
        JPanel page = LivroCaixaView.page(); UiKit.RoundPanel surface = new UiKit.RoundPanel(16).fill(Color.WHITE);
        surface.setLayout(new BorderLayout(0, 13)); surface.setBorder(new EmptyBorder(15, 18, 15, 18));
        surface.add(LivroCaixaView.title("Registrar pagamento", 29), BorderLayout.NORTH);
        JPanel main = new JPanel(new BorderLayout(0, 13)); main.setOpaque(false);
        UiKit.RoundPanel banner = new UiKit.RoundPanel(13).fill(new Color(231, 240, 255)).shadow(false); banner.setLayout(new GridLayout(1, 5)); banner.setBorder(new EmptyBorder(13, 16, 13, 16));
        banner.add(bannerStack("Cliente:", item.client)); banner.add(bannerStack("Fatura:", String.valueOf(item.appointmentId))); banner.add(bannerStack("Parcela:", item.label)); banner.add(bannerStack("Vencimento:", LivroCaixaView.fmt(item.due))); banner.add(bannerStack("Valor da parcela:", LivroCaixaView.money(item.value))); main.add(banner, BorderLayout.NORTH);

        JPanel columns = new JPanel(new GridLayout(1, 2, 15, 0)); columns.setOpaque(false);
        JPanel left = new JPanel(new BorderLayout(0, 11)); left.setOpaque(false);
        UiKit.RoundPanel form = new UiKit.RoundPanel(14).fill(new Color(248, 250, 255)).shadow(false); form.setLayout(new GridBagLayout()); form.setBorder(new EmptyBorder(12, 15, 12, 15));
        JComboBox<String> method = new JComboBox<>(frame.store.paymentMethods.toArray(String[]::new)); method.setSelectedItem("Cartão de crédito");
        BigDecimal suggestedDiscount = originalBalance.min(new BigDecimal("20.00"));
        JTextField received = LivroCaixaView.modernField(originalBalance.subtract(suggestedDiscount).toPlainString()), date = LivroCaixaView.modernField("05/11/2026"), discount = LivroCaixaView.modernField(suggestedDiscount.toPlainString());
        UiKit.SegmentToggle money = new UiKit.SegmentToggle("R$"), percent = new UiKit.SegmentToggle("%"); ButtonGroup discountModes = new ButtonGroup(); discountModes.add(money); discountModes.add(percent); money.setSelected(true); JPanel discountMode = new JPanel(new GridLayout(1,2));discountMode.setOpaque(false);discountMode.add(money);discountMode.add(percent);
        GridBagConstraints gc = new GridBagConstraints(); gc.fill=GridBagConstraints.HORIZONTAL;gc.weightx=1;gc.gridwidth=2;gc.gridx=0;gc.gridy=0;gc.insets=new Insets(0,0,8,0);form.add(fieldBox("Forma de pagamento",method),gc);gc.gridy++;form.add(fieldBox("Valor recebido",received),gc);gc.gridy++;gc.gridwidth=1;gc.weightx=.55;gc.insets=new Insets(0,0,0,9);form.add(fieldBox("Data",date),gc);gc.gridx=1;gc.weightx=.45;gc.insets=new Insets(0,0,0,0);JPanel discountLine=new JPanel(new BorderLayout(8,0));discountLine.setOpaque(false);discountLine.add(discountMode,BorderLayout.WEST);discountLine.add(discount);form.add(fieldBox("Desconto",discountLine),gc);left.add(form,BorderLayout.NORTH);
        Object[][] history = frame.store.payments.stream().filter(p -> p.appointmentId==item.appointmentId && p.label.equals(item.label) && !p.reversed).map(p -> new Object[]{LivroCaixaView.fmt(p.date),p.method,LivroCaixaView.money(p.value)}).toArray(Object[][]::new);
        JPanel historyPanel = tablePanel("Pagamentos já realizados", new String[]{"Data","Forma de pagamento","Valor"}, history); left.add(historyPanel); columns.add(left);

        JLabel installmentValue=summaryValue(LivroCaixaView.money(item.value),new Color(24,58,145));
        JLabel receivedValue=summaryValue(LivroCaixaView.money(item.paid),UiKit.BLUE);
        JLabel discountValue=summaryValue(LivroCaixaView.money(BigDecimal.ZERO),new Color(202,112,0));
        JLabel currentValue=summaryValue(LivroCaixaView.money(BigDecimal.ZERO),new Color(18,137,62));
        JLabel balanceValue=summaryValue(LivroCaixaView.money(originalBalance),new Color(202,112,0));
        UiKit.RoundPanel summary=paymentSummaryPanel(installmentValue,receivedValue,discountValue,currentValue,balanceValue);
        columns.add(summary);main.add(columns);surface.add(main);
        Runnable recalc=()->{try{BigDecimal payment=LivroCaixaView.parseMoney(received.getText()),rawDiscount=LivroCaixaView.parseMoney(discount.getText()),actualDiscount=percent.isSelected()?originalBalance.multiply(rawDiscount).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP):rawDiscount;BigDecimal balance=originalBalance.subtract(payment).subtract(actualDiscount);discountValue.setText(LivroCaixaView.money(actualDiscount));currentValue.setText(LivroCaixaView.money(payment));balanceValue.setText(LivroCaixaView.money(balance));styleBalance(balanceValue,balance);}catch(Exception ignored){balanceValue.setText("Revise os valores");styleSummaryCard(balanceValue,UiKit.RED,new Color(255,239,241),new Color(255,193,199));}};
        attach(received,recalc);attach(discount,recalc);money.addActionListener(e->recalc.run());percent.addActionListener(e->recalc.run());recalc.run();
        JPanel actions=new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0));actions.setOpaque(false);JButton back=UiKit.secondary("Voltar","swap"),cancel=UiKit.danger("Cancelar parcela"),confirm=UiKit.primary("Confirmar pagamento","wallet");
        back.addActionListener(e->frame.showPage("Contas a receber"));cancel.addActionListener(e->{if(LivroCaixaView.confirm(frame,"Cancelar a parcela "+item.label+" de "+item.client+"?")){item.cancelled=true;frame.showPage("Contas a receber");}});
        confirm.addActionListener(e->{try{BigDecimal payment=LivroCaixaView.parseMoney(received.getText()),rawDiscount=LivroCaixaView.parseMoney(discount.getText()),actualDiscount=percent.isSelected()?originalBalance.multiply(rawDiscount).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP):rawDiscount;if(payment.signum()<=0||actualDiscount.signum()<0||payment.add(actualDiscount).compareTo(originalBalance)>0)throw new IllegalArgumentException();item.paid=item.paid.add(payment).add(actualDiscount);frame.store.payments.add(new Payment(frame.store.nextPayment++,item.appointmentId,item.client,item.label,LivroCaixaView.parseDate(date.getText()),payment,String.valueOf(method.getSelectedItem()),actualDiscount,false));frame.store.save();frame.paymentConfirmed(item);}catch(Exception ex){LivroCaixaView.error(frame,"Revise o pagamento, o desconto e a data.");}});
        actions.add(back);actions.add(cancel);actions.add(confirm);surface.add(actions,BorderLayout.SOUTH);page.add(surface);frame.showCustom("Contas a receber",page);
    }

    private static JPanel bannerStack(String label,String value){JPanel p=new JPanel();p.setOpaque(false);p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));JLabel a=new JLabel(label);a.setFont(new Font("Segoe UI",Font.BOLD,15));JLabel b=LivroCaixaView.title(value,21);b.setForeground(new Color(23,58,145));p.add(a);p.add(Box.createVerticalStrut(7));p.add(b);return p;}
    private static JPanel fieldBox(String label,JComponent component){JPanel p=new JPanel(new BorderLayout(0,4));p.setOpaque(false);JLabel l=new JLabel(label);l.setFont(new Font("Segoe UI",Font.BOLD,15));p.add(l,BorderLayout.NORTH);component.setPreferredSize(new Dimension(130,38));p.add(component);return p;}
    private static JLabel summaryValue(String text,Color color){JLabel label=LivroCaixaView.title(text,19);label.setForeground(color);label.setHorizontalAlignment(SwingConstants.RIGHT);return label;}
    private static UiKit.RoundPanel paymentSummaryPanel(JLabel installment,JLabel received,JLabel discount,JLabel current,JLabel balance){
        UiKit.RoundPanel panel=new UiKit.RoundPanel(14).fill(new Color(247,249,255)).line(new Color(210,222,239)).shadow(false);
        panel.setLayout(new BorderLayout(0,12));
        panel.setBorder(new EmptyBorder(15,16,15,16));
        JPanel header=new JPanel();header.setOpaque(false);header.setLayout(new BoxLayout(header,BoxLayout.Y_AXIS));
        header.add(LivroCaixaView.title("Resumo do recebimento",22));
        JLabel hint=new JLabel("Valores atualizados automaticamente");hint.setFont(new Font("Segoe UI",Font.PLAIN,14));hint.setForeground(UiKit.MUTED);header.add(Box.createVerticalStrut(3));header.add(hint);
        panel.add(header,BorderLayout.NORTH);

        JPanel content=new JPanel();content.setOpaque(false);content.setLayout(new BoxLayout(content,BoxLayout.Y_AXIS));
        UiKit.RoundPanel hero=new UiKit.RoundPanel(13).fill(new Color(232,241,255)).line(new Color(185,208,241)).shadow(false);
        hero.setLayout(new BorderLayout(14,0));hero.setBorder(new EmptyBorder(14,15,14,15));hero.setAlignmentX(Component.LEFT_ALIGNMENT);hero.setMaximumSize(new Dimension(Integer.MAX_VALUE,92));
        UiKit.RoundPanel heroIcon=new UiKit.RoundPanel(11).fill(new Color(215,231,255)).line(null).shadow(false);heroIcon.setPreferredSize(new Dimension(54,54));heroIcon.setLayout(new GridBagLayout());heroIcon.add(new JLabel(UiKit.vectorIcon("wallet",27,UiKit.BLUE)));hero.add(heroIcon,BorderLayout.WEST);
        JPanel heroText=new JPanel();heroText.setOpaque(false);heroText.setLayout(new BoxLayout(heroText,BoxLayout.Y_AXIS));JLabel heroLabel=new JLabel("Valor da parcela");heroLabel.setFont(new Font("Segoe UI",Font.BOLD,14));heroLabel.setForeground(UiKit.MUTED);installment.setFont(new Font("Segoe UI",Font.BOLD,25));installment.setHorizontalAlignment(SwingConstants.LEFT);heroText.add(heroLabel);heroText.add(Box.createVerticalStrut(5));heroText.add(installment);hero.add(heroText);content.add(hero);content.add(Box.createVerticalStrut(11));

        JPanel metrics=new JPanel(new GridLayout(2,2,10,10));metrics.setOpaque(false);metrics.setAlignmentX(Component.LEFT_ALIGNMENT);metrics.setMaximumSize(new Dimension(Integer.MAX_VALUE,190));
        metrics.add(summaryCard("Já recebido",received,"coins",UiKit.BLUE,new Color(235,243,255),new Color(197,216,244)));
        metrics.add(summaryCard("Desconto",discount,"arrowDown",new Color(202,112,0),new Color(255,247,229),new Color(246,218,160)));
        metrics.add(summaryCard("Pagamento atual",current,"wallet",new Color(18,137,62),new Color(235,249,239),new Color(184,226,197)));
        metrics.add(summaryCard("Saldo restante",balance,"calendar",new Color(202,112,0),new Color(255,247,229),new Color(246,218,160)));
        content.add(metrics);content.add(Box.createVerticalGlue());
        JPanel holder=new JPanel(new BorderLayout());holder.setOpaque(false);holder.add(content,BorderLayout.NORTH);panel.add(holder);
        return panel;
    }
    private static JComponent summaryCard(String label,JLabel value,String icon,Color accent,Color fill,Color line){UiKit.RoundPanel card=new UiKit.RoundPanel(12).fill(fill).line(line).shadow(false);card.setLayout(new BorderLayout(10,0));card.setBorder(new EmptyBorder(11,12,11,12));UiKit.RoundPanel iconBox=new UiKit.RoundPanel(9).fill(new Color(accent.getRed(),accent.getGreen(),accent.getBlue(),24)).line(null).shadow(false);iconBox.setPreferredSize(new Dimension(40,40));iconBox.setLayout(new GridBagLayout());iconBox.add(new JLabel(UiKit.vectorIcon(icon,20,accent)));card.add(iconBox,BorderLayout.WEST);JPanel text=new JPanel();text.setOpaque(false);text.setLayout(new BoxLayout(text,BoxLayout.Y_AXIS));JLabel title=new JLabel(label);title.setFont(new Font("Segoe UI",Font.BOLD,13));title.setForeground(UiKit.MUTED);value.setFont(new Font("Segoe UI",Font.BOLD,17));value.setHorizontalAlignment(SwingConstants.LEFT);text.add(title);text.add(Box.createVerticalStrut(3));text.add(value);card.add(text);value.putClientProperty("summaryCard",card);return card;}
    private static void styleBalance(JLabel value,BigDecimal balance){if(balance.signum()<0)styleSummaryCard(value,UiKit.RED,new Color(255,239,241),new Color(255,193,199));else if(balance.signum()==0)styleSummaryCard(value,new Color(18,137,62),new Color(235,249,239),new Color(184,226,197));else styleSummaryCard(value,new Color(202,112,0),new Color(255,247,229),new Color(246,218,160));}
    private static void styleSummaryCard(JLabel value,Color color,Color fill,Color line){value.setForeground(color);Object property=value.getClientProperty("summaryCard");if(property instanceof UiKit.RoundPanel card){card.fill(fill).line(line);card.repaint();}}
    private static JPanel tablePanel(String heading,String[] columns,Object[][] rows){UiKit.RoundPanel p=new UiKit.RoundPanel(13).fill(new Color(247,249,255)).shadow(false);p.setLayout(new BorderLayout(0,6));p.setBorder(new EmptyBorder(9,9,9,9));p.add(LivroCaixaView.title(heading,18),BorderLayout.NORTH);DefaultTableModel model=new DefaultTableModel(rows,columns){@Override public boolean isCellEditable(int r,int c){return false;}};JTable table=UiKit.modernTable(model);table.setRowHeight(35);JScrollPane scroll=new JScrollPane(table);scroll.setBorder(new LineBorder(UiKit.LINE));p.add(scroll);return p;}
    private static void attach(JTextField field,Runnable runnable){field.getDocument().addDocumentListener(new DocumentListener(){public void insertUpdate(DocumentEvent e){runnable.run();}public void removeUpdate(DocumentEvent e){runnable.run();}public void changedUpdate(DocumentEvent e){runnable.run();}});}
}
