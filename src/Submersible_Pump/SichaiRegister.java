package Submersible_Pump;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * सिंचाई पंप रजिस्टर - Java Swing Desktop App
 * हर किसान का अलग रिकॉर्ड, हर एंट्री में तारीख/फसल/समय/पैसा (₹60 प्रति घंटा default)
 * डेटा "sichai_data.txt" फाइल में सेव होता है (सिंपल फाइल-डेटाबेस)।
 */
public class SichaiRegister extends JFrame {

    // ===== Data Models =====
    static class Farmer {
        String id;
        String name;
        Farmer(String id, String name) { this.id = id; this.name = name; }
    }

    static class Entry {
        String id;
        String farmerId;
        String date;     // yyyy-MM-dd
        String crop;
        int hours;
        int minutes;
        double rate;
        double cost;

        Entry(String id, String farmerId, String date, String crop, int hours, int minutes, double rate) {
            this.id = id;
            this.farmerId = farmerId;
            this.date = date;
            this.crop = crop;
            this.hours = hours;
            this.minutes = minutes;
            this.rate = rate;
            this.cost = Math.round(((hours * 60 + minutes) / 60.0) * rate * 100.0) / 100.0;
        }
    }

    static class Payment {
        String id;
        String farmerId;
        String date;
        double amount;
        Payment(String id, String farmerId, String date, double amount) {
            this.id = id;
            this.farmerId = farmerId;
            this.date = date;
            this.amount = amount;
        }
    }

    // ===== Storage =====
    static final String DATA_FILE = "sichai_data.txt";
    List<Farmer> farmers = new ArrayList<>();
    List<Entry> entries = new ArrayList<>();
    List<Payment> payments = new ArrayList<>();

    // ===== UI Components =====
    DefaultListModel<Farmer> farmerListModel = new DefaultListModel<>();
    JList<Farmer> farmerJList = new JList<>(farmerListModel);
    DefaultTableModel entryTableModel;
    JTable entryTable;
    JLabel summaryLabel;
    JTextField nameField, hoursField, minutesField, rateField, paymentField;
    JComboBox<String> cropBox;
    JTextField dateField;
    DefaultTableModel paymentTableModel;
    JTable paymentTable;
    Farmer activeFarmer = null;

    static final String[] CROPS = {"गेहूं", "धान", "बेहन", "गन्ना","चरी", "सरसो", "पलेवा", "पिछला", "सब्जी", "बेहन", "अन्य"};

    public SichaiRegister() {
        super("सिंचाई पंप रजिस्टर");
        loadData();
        buildUI();
        refreshFarmerList();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
    }

    // ===== UI Construction =====
    void buildUI() {
        setLayout(new BorderLayout(10, 10));

        // ---- Left panel: farmer list ----
        JPanel left = new JPanel(new BorderLayout(5, 5));
        left.setBorder(BorderFactory.createTitledBorder("किसान सूची"));
        farmerJList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel l = new JLabel("  " + value.name);
            l.setOpaque(true);
            l.setBackground(isSelected ? new Color(62, 107, 79) : Color.WHITE);
            l.setForeground(isSelected ? Color.WHITE : Color.BLACK);
            l.setFont(new Font("SansSerif", Font.PLAIN, 14));
            l.setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 4));
            return l;
        });
        farmerJList.addListSelectionListener(e -> {
            activeFarmer = farmerJList.getSelectedValue();
            refreshEntryTable();
        });
        left.add(new JScrollPane(farmerJList), BorderLayout.CENTER);

        JPanel addFarmerPanel = new JPanel(new BorderLayout(5, 5));
        nameField = new JTextField();
        JButton addFarmerBtn = new JButton("+ किसान जोड़ें");
        addFarmerBtn.addActionListener(e -> addFarmer());
        nameField.addActionListener(e -> addFarmer());
        addFarmerPanel.add(nameField, BorderLayout.CENTER);
        addFarmerPanel.add(addFarmerBtn, BorderLayout.EAST);
        left.add(addFarmerPanel, BorderLayout.SOUTH);

        JButton deleteFarmerBtn = new JButton("किसान हटाएं");
        deleteFarmerBtn.addActionListener(e -> deleteFarmer());
        left.add(deleteFarmerBtn, BorderLayout.NORTH);

        left.setPreferredSize(new Dimension(220, 0));
        add(left, BorderLayout.WEST);

        // ---- Right panel: entry form + table ----
        JPanel right = new JPanel(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(2, 5, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("नई सिंचाई एंट्री"));

        dateField = new JTextField(java.time.LocalDate.now().toString());
        cropBox = new JComboBox<>(CROPS);
        hoursField = new JTextField("0");
        minutesField = new JTextField("0");
        rateField = new JTextField("60");

        form.add(new JLabel("तारीख (yyyy-MM-dd):"));
        form.add(new JLabel("फसल:"));
        form.add(new JLabel("घंटे:"));
        form.add(new JLabel("मिनट:"));
        form.add(new JLabel("रेट (₹/घंटा):"));

        form.add(dateField);
        form.add(cropBox);
        form.add(hoursField);
        form.add(minutesField);
        form.add(rateField);

        JButton addEntryBtn = new JButton("एंट्री जोड़ें");
        addEntryBtn.addActionListener(e -> addEntry());

        JPanel formWrap = new JPanel(new BorderLayout());
        formWrap.add(form, BorderLayout.CENTER);
        formWrap.add(addEntryBtn, BorderLayout.SOUTH);
        right.add(formWrap, BorderLayout.NORTH);

        entryTableModel = new DefaultTableModel(
                new String[]{"तारीख", "फसल", "घंटे", "मिनट", "रेट", "पैसा (₹)"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        entryTable = new JTable(entryTableModel);
        JPanel entryTablePanel = new JPanel(new BorderLayout(5, 5));
        entryTablePanel.setBorder(BorderFactory.createTitledBorder("सिंचाई रिकॉर्ड (कॉपी)"));
        entryTablePanel.add(new JScrollPane(entryTable), BorderLayout.CENTER);
        JButton deleteEntryBtn = new JButton("चुनी हुई एंट्री हटाएं");
        deleteEntryBtn.addActionListener(e -> deleteSelectedEntry());
        entryTablePanel.add(deleteEntryBtn, BorderLayout.SOUTH);

        // ---- Payment section ----
        paymentTableModel = new DefaultTableModel(new String[]{"तारीख", "जमा राशि (₹)"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        paymentTable = new JTable(paymentTableModel);
        JPanel paymentPanel = new JPanel(new BorderLayout(5, 5));
        paymentPanel.setBorder(BorderFactory.createTitledBorder("पैसा जमा (भुगतान)"));

        JPanel paymentForm = new JPanel(new BorderLayout(5, 5));
        paymentField = new JTextField();
        JButton addPaymentBtn = new JButton("पैसा जमा करें");
        addPaymentBtn.addActionListener(e -> addPayment());
        paymentField.addActionListener(e -> addPayment());
        paymentForm.add(new JLabel("राशि ₹: "), BorderLayout.WEST);
        paymentForm.add(paymentField, BorderLayout.CENTER);
        paymentForm.add(addPaymentBtn, BorderLayout.EAST);

        JButton deletePaymentBtn = new JButton("चुना हुआ भुगतान हटाएं");
        deletePaymentBtn.addActionListener(e -> deleteSelectedPayment());

        JPanel paymentBottom = new JPanel(new BorderLayout());
        paymentBottom.add(paymentForm, BorderLayout.NORTH);
        paymentBottom.add(deletePaymentBtn, BorderLayout.SOUTH);

        paymentPanel.add(new JScrollPane(paymentTable), BorderLayout.CENTER);
        paymentPanel.add(paymentBottom, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, entryTablePanel, paymentPanel);
        split.setResizeWeight(0.6);

        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        tablePanel.add(split, BorderLayout.CENTER);

        summaryLabel = new JLabel("Total Sichai: ₹0   |   Total Paid: ₹0   |   Due: ₹0");
        summaryLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        summaryLabel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        tablePanel.add(summaryLabel, BorderLayout.SOUTH);

        right.add(tablePanel, BorderLayout.CENTER);

        add(right, BorderLayout.CENTER);
    }

    // ===== Actions =====
    void addFarmer() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "किसान का नाम लिखें।");
            return;
        }
        Farmer f = new Farmer(UUID.randomUUID().toString(), name);
        farmers.add(0, f);
        nameField.setText("");
        saveData();
        refreshFarmerList();
    }

    void deleteFarmer() {
        Farmer f = farmerJList.getSelectedValue();
        if (f == null) return;
        int confirm = JOptionPane.showConfirmDialog(this,
                f.name + " को हटाएं? इनकी सारी एंट्री भी मिट जाएंगी।",
                "पुष्टि करें", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        farmers.remove(f);
        entries.removeIf(e -> f.id.equals(e.farmerId));
        payments.removeIf(p -> f.id.equals(p.farmerId));
        activeFarmer = null;
        saveData();
        refreshFarmerList();
        refreshEntryTable();
    }

    void addEntry() {
        if (activeFarmer == null) {
            JOptionPane.showMessageDialog(this, "पहले लिस्ट से एक किसान चुनें।");
            return;
        }
        try {
            String date = dateField.getText().trim();
            String crop = (String) cropBox.getSelectedItem();
            int h = parseIntSafe(hoursField.getText());
            int m = parseIntSafe(minutesField.getText());
            double rate = Double.parseDouble(rateField.getText().trim());
            if (h == 0 && m == 0) {
                JOptionPane.showMessageDialog(this, "कृपया घंटा या मिनट भरें।");
                return;
            }
            if (m >= 60) {
                JOptionPane.showMessageDialog(this, "मिनट 60 से कम होना चाहिए।");
                return;
            }
            Entry entry = new Entry(UUID.randomUUID().toString(), activeFarmer.id, date, crop, h, m, rate);
            entries.add(0, entry);
            saveData();
            refreshEntryTable();
            hoursField.setText("0");
            minutesField.setText("0");
            rateField.setText("60");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "घंटे/मिनट/रेट सही नंबर में भरें।");
        }
    }

    void deleteSelectedEntry() {
        int row = entryTable.getSelectedRow();
        if (row < 0 || activeFarmer == null) return;
        List<Entry> farmerEntries = entriesForFarmer(activeFarmer.id);
        Entry toRemove = farmerEntries.get(row);
        entries.remove(toRemove);
        saveData();
        refreshEntryTable();
    }

    void addPayment() {
        if (activeFarmer == null) {
            JOptionPane.showMessageDialog(this, "पहले लिस्ट से एक किसान चुनें।");
            return;
        }
        try {
            double amount = Double.parseDouble(paymentField.getText().trim());
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "सही राशि भरें।");
                return;
            }
            String date = java.time.LocalDate.now().toString();
            payments.add(0, new Payment(UUID.randomUUID().toString(), activeFarmer.id, date, amount));
            paymentField.setText("");
            saveData();
            refreshEntryTable();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "राशि सही नंबर में भरें।");
        }
    }

    void deleteSelectedPayment() {
        int row = paymentTable.getSelectedRow();
        if (row < 0 || activeFarmer == null) return;
        List<Payment> farmerPayments = paymentsForFarmer(activeFarmer.id);
        payments.remove(farmerPayments.get(row));
        saveData();
        refreshEntryTable();
    }

    int parseIntSafe(String s) {
        s = s.trim();
        return s.isEmpty() ? 0 : Integer.parseInt(s);
    }

    // ===== Refresh helpers =====
    List<Entry> entriesForFarmer(String farmerId) {
        List<Entry> list = new ArrayList<>();
        for (Entry e : entries) if (farmerId.equals(e.farmerId)) list.add(e);
        return list;
    }

    List<Payment> paymentsForFarmer(String farmerId) {
        List<Payment> list = new ArrayList<>();
        for (Payment p : payments) if (farmerId.equals(p.farmerId)) list.add(p);
        return list;
    }

    void refreshFarmerList() {
        farmerListModel.clear();
        for (Farmer f : farmers) farmerListModel.addElement(f);
    }

    void refreshEntryTable() {
        entryTableModel.setRowCount(0);
        paymentTableModel.setRowCount(0);
        if (activeFarmer == null) {
            summaryLabel.setText("Total Sichai: ₹0   |   Total Paid: ₹0   |   Due: ₹0");
            return;
        }
        List<Entry> list = entriesForFarmer(activeFarmer.id);
        double totalCost = 0;
        for (Entry e : list) {
            entryTableModel.addRow(new Object[]{
                    e.date, e.crop, e.hours, e.minutes, e.rate, String.format("%.2f", e.cost)
            });
            totalCost += e.cost;
        }
        List<Payment> pList = paymentsForFarmer(activeFarmer.id);
        double totalPaid = 0;
        for (Payment p : pList) {
            paymentTableModel.addRow(new Object[]{p.date, String.format("%.2f", p.amount)});
            totalPaid += p.amount;
        }
        double due = totalCost - totalPaid;
        summaryLabel.setText(String.format(
                "Total Sichai: ₹%.2f   |   Total Paid: ₹%.2f   |   Due: ₹%.2f",
                totalCost, totalPaid, due));
    }

    // ===== File-based persistence (simple database) =====
    // Format per line:
    // FARMER|id|name
    // ENTRY|id|farmerId|date|crop|hours|minutes|rate|cost
    void saveData() {
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(DATA_FILE), "UTF-8"))) {
            for (Farmer f : farmers) {
                pw.println("FARMER|" + f.id + "|" + escape(f.name));
            }
            for (Entry e : entries) {
                pw.println("ENTRY|" + e.id + "|" + e.farmerId + "|" + e.date + "|" +
                        escape(e.crop) + "|" + e.hours + "|" + e.minutes + "|" + e.rate + "|" + e.cost);
            }
            for (Payment p : payments) {
                pw.println("PAYMENT|" + p.id + "|" + p.farmerId + "|" + p.date + "|" + p.amount);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "डेटा सेव करने में समस्या हुई: " + ex.getMessage());
        }
    }

    void loadData() {
        File f = new File(DATA_FILE);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(f), "UTF-8"))) {
            String line;
            int lineNum = 0;
            while ((line = br.readLine()) != null) {
                lineNum++;
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("\\|", -1);
                try {
                    if (parts[0].equals("FARMER") && parts.length >= 3) {
                        farmers.add(new Farmer(parts[1], unescape(parts[2])));
                    } else if (parts[0].equals("ENTRY") && parts.length >= 8) {
                        Entry e = new Entry(
                                parts[1], parts[2], parts[3], unescape(parts[4]),
                                Integer.parseInt(parts[5]), Integer.parseInt(parts[6]),
                                Double.parseDouble(parts[7])
                        );
                        entries.add(e);
                    } else if (parts[0].equals("PAYMENT") && parts.length >= 5) {
                        payments.add(new Payment(parts[1], parts[2], parts[3], Double.parseDouble(parts[4])));
                    } else {
                        System.out.println("डेटा फाइल की लाइन " + lineNum + " को नज़रअंदाज़ किया गया (गलत फॉर्मेट): " + line);
                    }
                } catch (Exception lineEx) {
                    System.out.println("डेटा फाइल की लाइन " + lineNum + " पढ़ने में समस्या, नज़रअंदाज़ किया गया: " + line);
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "डेटा लोड करने में समस्या हुई: " + ex.getMessage());
        }
    }

    String escape(String s) { return s.replace("|", "/"); }
    String unescape(String s) { return s; }

    public static void main(String[] args) {
        printAvailableFonts();
        setHindiFont();
        SwingUtilities.invokeLater(() -> new SichaiRegister().setVisible(true));
    }

    /** Debug ke liye: terminal me system ke saare fonts print karta hai, taaki pata chale Hindi font hai ya nahi */
    static void printAvailableFonts() {
        String[] availableFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        System.out.println("==== System me available fonts (Hindi-related) ====");
        for (String f : availableFonts) {
            String lower = f.toLowerCase();
            if (lower.contains("nirmala") || lower.contains("mangal") || lower.contains("devanagari")
                    || lower.contains("kruti") || lower.contains("hindi") || lower.contains("lohit")
                    || lower.contains("noto sans deva")) {
                System.out.println("  -> " + f);
            }
        }
        System.out.println("====================================================");
    }

    /**
     * Java Swing के default font में Devanagari (हिंदी) glyphs नहीं होते,
     * इसलिए सही दिखने के लिए सिस्टम में मौजूद कोई Hindi-सपोर्ट वाला फॉन्ट सेट करते हैं।
     */
    static void setHindiFont() {
        String[] candidates = {
                "Nirmala UI",            // Windows में by default होता है
                "Noto Sans Devanagari",  // अगर अलग से installed हो
                "Mangal",                // पुराने Windows में होता है
                "Lohit Devanagari"       // Linux में अक्सर होता है
        };
        String[] availableFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        java.util.Set<String> available = new HashSet<>(Arrays.asList(availableFonts));

        String chosen = null;
        for (String c : candidates) {
            if (available.contains(c)) {
                chosen = c;
                break;
            }
        }
        if (chosen == null) {
            // कुछ न मिले तो भी आगे बढ़ें, default font इस्तेमाल होगा
            return;
        }
        Font font = new Font(chosen, Font.PLAIN, 14);
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, new javax.swing.plaf.FontUIResource(font));
            }
        }
    }
}

