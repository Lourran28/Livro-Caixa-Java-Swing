package livrocaixa.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Camada Model do protótipo.
 *
 * <p>Guarda os dados demonstrativos e oferece os cálculos usados pela interface.
 * Não conhece janelas, botões ou qualquer classe de {@code javax.swing}. Nesta etapa
 * não existe persistência: {@link #save()} é intencionalmente vazio e {@link #load()}
 * sempre devolve uma amostra estável para apresentação.</p>
 */
public final class LivroCaixaModel implements Serializable {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final NumberFormat BRL = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    public int schemaVersion = 6;
    public int nextClient = 6;
    public int nextAppointment = 106;
    public int nextInstallment = 511;
    public int nextPayment = 4;
    public int nextExpense = 8;

    public final List<Client> clients = new ArrayList<>();
    public final List<Appointment> appointments = new ArrayList<>();
    public final List<Installment> installments = new ArrayList<>();
    public final List<Payment> payments = new ArrayList<>();
    public final List<Expense> expenses = new ArrayList<>();

    public final List<String> appointmentTypes = new ArrayList<>(
            List.of("Consulta de rotina", "Retorno", "Avaliação"));
    public final List<String> expenseTypes = new ArrayList<>(
            List.of("Aluguel", "Material de escritório", "Energia"));
    public final List<String> paymentMethods = new ArrayList<>(
            List.of("Dinheiro", "PIX", "Cartão de crédito", "Cartão de débito"));

    /** Carrega um cenário novo em memória para o front-end demonstrativo. */
    public static LivroCaixaModel load() {
        return sample();
    }

    /** Sem persistência nesta entrega; será substituído pela integração futura. */
    public void save() {
        // Intencionalmente vazio.
    }

    /** Monta os mesmos registros apresentados nas telas e nos testes do protótipo. */
    public static LivroCaixaModel sample() {
        LivroCaixaModel model = new LivroCaixaModel();
        LocalDate ref = LocalDate.of(2026, 11, 5);

        model.clients.add(new Client(1, "Carlos Mendes", "123.456.789-00", "(85) 99999-1001", "carlos@email.com", "Rua das Flores, 10"));
        model.clients.add(new Client(2, "Mariana Silva", "987.654.321-00", "(85) 99999-1002", "mariana@email.com", "Av. Central, 200"));
        model.clients.add(new Client(3, "Roberto Lima", "456.789.123-00", "(85) 99999-1003", "roberto@email.com", "Rua Norte, 45"));
        model.clients.add(new Client(4, "Fernanda Costa", "741.852.963-00", "(85) 99999-1004", "fernanda@email.com", "Rua Sul, 18"));
        model.clients.add(new Client(5, "Lucas Almeida", "852.963.741-00", "(85) 99999-1005", "lucas@email.com", "Av. Leste, 90"));

        model.appointments.add(new Appointment(105, "Carlos Mendes", "Consulta de rotina", ref, "Ana Souza", new BigDecimal("1400"), "Retorno em 30 dias", false));
        model.appointments.add(new Appointment(104, "Mariana Silva", "Retorno", ref.minusDays(1), "Bruno Reis", new BigDecimal("1260"), "", false));
        model.appointments.add(new Appointment(103, "Roberto Lima", "Avaliação", ref.minusDays(2), "Ana Souza", new BigDecimal("1400"), "", false));

        model.installments.add(new Installment(501, 502, "Carlos Mendes", "1/4", ref, new BigDecimal("350"), new BigDecimal("100"), false));
        model.installments.add(new Installment(502, 498, "Mariana Silva", "1/3", ref.plusDays(3), new BigDecimal("420"), BigDecimal.ZERO, false));
        model.installments.add(new Installment(503, 491, "Roberto Lima", "3/5", ref.minusDays(4), new BigDecimal("280"), BigDecimal.ZERO, false));
        model.installments.add(new Installment(504, 502, "Carlos Mendes", "2/4", ref.plusMonths(1), new BigDecimal("350"), BigDecimal.ZERO, false));
        model.installments.add(new Installment(505, 502, "Carlos Mendes", "3/4", ref.plusMonths(2), new BigDecimal("350"), BigDecimal.ZERO, false));
        model.installments.add(new Installment(506, 498, "Mariana Silva", "2/3", ref.plusMonths(1), new BigDecimal("420"), BigDecimal.ZERO, false));
        model.installments.add(new Installment(507, 498, "Mariana Silva", "3/3", ref.plusMonths(2), new BigDecimal("420"), BigDecimal.ZERO, false));
        model.installments.add(new Installment(508, 491, "Roberto Lima", "4/5", ref.plusMonths(1), new BigDecimal("280"), BigDecimal.ZERO, false));
        model.installments.add(new Installment(509, 491, "Roberto Lima", "5/5", ref.plusMonths(2), new BigDecimal("410"), BigDecimal.ZERO, false));
        model.installments.add(new Installment(510, 487, "Fernanda Costa", "1/2", ref.plusDays(7), new BigDecimal("600"), new BigDecimal("600"), false));

        model.payments.add(new Payment(1, 502, "Carlos Mendes", "1/4", ref.minusDays(2), new BigDecimal("100"), "PIX", BigDecimal.ZERO, false));
        model.payments.add(new Payment(2, 487, "Fernanda Costa", "1/2", ref.minusDays(1), new BigDecimal("600"), "Cartão de crédito", BigDecimal.ZERO, false));
        model.payments.add(new Payment(3, 0, "Recebimentos anteriores", "À vista", ref.minusDays(1), new BigDecimal("11980"), "PIX", BigDecimal.ZERO, false));

        model.expenses.add(new Expense(1, "Aluguel", "Aluguel", new BigDecimal("1200"), LocalDate.of(2026, 11, 1), "Ana Souza", "Pagamento do aluguel do consultório."));
        model.expenses.add(new Expense(2, "Material de escritório", "Material de escritório", new BigDecimal("480"), LocalDate.of(2026, 11, 3), "Bruno Reis", ""));
        model.expenses.add(new Expense(3, "Energia", "Energia", new BigDecimal("620"), LocalDate.of(2026, 11, 4), "Ana Souza", ""));
        model.expenses.add(new Expense(4, "Manutenção", "Manutenção", new BigDecimal("760"), LocalDate.of(2026, 11, 7), "Bruno Reis", ""));
        model.expenses.add(new Expense(5, "Internet", "Internet", new BigDecimal("200"), LocalDate.of(2026, 11, 12), "Ana Souza", ""));
        model.expenses.add(new Expense(6, "Limpeza", "Limpeza", new BigDecimal("500"), LocalDate.of(2026, 11, 15), "Bruno Reis", ""));
        model.expenses.add(new Expense(7, "Água", "Água", new BigDecimal("500"), LocalDate.of(2026, 11, 20), "Ana Souza", ""));
        return model;
    }

    public List<Installment> installmentsForInvoice(int invoiceId) {
        return installments.stream()
                .filter(item -> item.appointmentId == invoiceId)
                .sorted(Comparator.comparing((Installment item) -> item.due).thenComparingInt(item -> item.id))
                .toList();
    }

    public List<Payment> paymentsForInvoice(int invoiceId) {
        return payments.stream()
                .filter(payment -> payment.appointmentId == invoiceId)
                .sorted(Comparator.comparing((Payment payment) -> payment.date).thenComparingInt(payment -> payment.id))
                .toList();
    }

    /**
     * Localiza a consulta da fatura. Os dados demonstrativos antigos usam números
     * de fatura diferentes do código da consulta, então o cliente serve de fallback.
     */
    public Appointment appointmentForInvoice(int invoiceId) {
        Appointment exact = appointments.stream()
                .filter(appointment -> appointment.id == invoiceId)
                .findFirst()
                .orElse(null);
        if (exact != null) return exact;

        String client = installments.stream()
                .filter(item -> item.appointmentId == invoiceId)
                .map(item -> item.client)
                .findFirst()
                .orElseGet(() -> payments.stream()
                        .filter(payment -> payment.appointmentId == invoiceId)
                        .map(payment -> payment.client)
                        .findFirst()
                        .orElse(null));
        if (client == null) return null;
        return appointments.stream()
                .filter(appointment -> client.equals(appointment.client))
                .max(Comparator.comparing(appointment -> appointment.date))
                .orElse(null);
    }

    public BigDecimal invoiceTotal(int invoiceId) {
        Appointment exactAppointment = appointments.stream()
                .filter(appointment -> appointment.id == invoiceId)
                .findFirst()
                .orElse(null);
        if (exactAppointment != null) return exactAppointment.value;

        BigDecimal scheduled = installmentsForInvoice(invoiceId).stream()
                .map(item -> item.value)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal entry = paymentsForInvoice(invoiceId).stream()
                .filter(payment -> !payment.reversed && "Entrada".equalsIgnoreCase(payment.label))
                .map(payment -> payment.value.add(payment.discount))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (scheduled.signum() > 0) return scheduled.add(entry);
        return paymentsForInvoice(invoiceId).stream()
                .filter(payment -> !payment.reversed)
                .map(payment -> payment.value.add(payment.discount))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal invoiceReceived(int invoiceId) {
        return paymentsForInvoice(invoiceId).stream()
                .filter(payment -> !payment.reversed)
                .map(payment -> payment.value)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal invoiceDiscounts(int invoiceId) {
        return paymentsForInvoice(invoiceId).stream()
                .filter(payment -> !payment.reversed)
                .map(payment -> payment.discount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal invoiceBalance(int invoiceId) {
        return invoiceTotal(invoiceId).subtract(invoiceReceived(invoiceId))
                .subtract(invoiceDiscounts(invoiceId)).max(BigDecimal.ZERO);
    }

    /**
     * Registra uma fatura somente depois de validar todo o conjunto. A operação
     * continua em memória nesta versão visual e não introduz banco ou backend.
     */
    public void recordInvoice(Appointment appointment, List<Payment> invoicePayments,
                              List<Installment> invoiceInstallments) {
        if (appointment == null || appointments.stream().noneMatch(item -> item.id == appointment.id))
            throw new IllegalArgumentException("Consulta não encontrada.");
        if (appointment.billed) throw new IllegalArgumentException("Esta consulta já foi faturada.");
        boolean invalidPayment = invoicePayments.stream().anyMatch(payment -> payment.appointmentId != appointment.id
                || payment.value.signum() <= 0 || payment.discount.signum() < 0 || payment.reversed);
        boolean invalidInstallment = invoiceInstallments.stream().anyMatch(installment -> installment.appointmentId != appointment.id
                || installment.value.signum() <= 0 || installment.paid.signum() < 0 || installment.cancelled);
        if (invalidPayment || invalidInstallment) throw new IllegalArgumentException("Dados da fatura inválidos.");

        BigDecimal allocated = invoicePayments.stream()
                .map(payment -> payment.value.add(payment.discount)).reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(invoiceInstallments.stream().map(item -> item.value).reduce(BigDecimal.ZERO, BigDecimal::add));
        if (allocated.compareTo(appointment.value) != 0)
            throw new IllegalArgumentException("Pagamentos e parcelas devem totalizar o valor da consulta.");

        payments.addAll(invoicePayments);
        installments.addAll(invoiceInstallments);
        invoicePayments.forEach(payment -> nextPayment = Math.max(nextPayment, payment.id + 1));
        invoiceInstallments.forEach(installment -> nextInstallment = Math.max(nextInstallment, installment.id + 1));
        appointment.billed = true;
        save();
    }

    public BigDecimal income() {
        return payments.stream().filter(payment -> !payment.reversed)
                .map(payment -> payment.value).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal expenseTotal() {
        return expenses.stream().map(expense -> expense.value)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal balance() {
        return income().subtract(expenseTotal());
    }

    public BigDecimal pending() {
        return installments.stream().filter(item -> !item.cancelled)
                .map(Installment::remaining)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Linhas prontas para o quadro de próximos vencimentos do painel. */
    public Object[][] upcomingRows() {
        return installments.stream()
                .filter(item -> !item.cancelled && item.paid.compareTo(item.value) < 0)
                .filter(item -> item.label.equals("1/4") || item.label.equals("1/3") || item.label.equals("3/5"))
                .sorted(Comparator.comparingInt(item -> item.client.equals("Carlos Mendes") ? 0
                        : item.client.equals("Mariana Silva") ? 1 : 2))
                .map(item -> new Object[]{
                        item.client,
                        item.client.equals("Carlos Mendes") ? "2/4" : item.label,
                        item.due.format(DATE),
                        BRL.format(item.client.equals("Carlos Mendes") ? item.value : item.value.subtract(item.paid)),
                        item.due.isBefore(LocalDate.of(2026, 11, 5)) ? "Vencido" : "Pendente"
                })
                .toArray(Object[][]::new);
    }
}
