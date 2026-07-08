package Student_Grade_Tracker;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

class student{
    String name;
    int grade;
    student(String name, int grade){
        this.name = name;
        this.grade = grade;
    }
}
public class Grade_Tracker_GUI extends JFrame {
    private JTextField nameField, gradeField;
    private JButton addButton, reportButton;
    private JTable table;
    private DefaultTableModel tableModel;
    private ArrayList<student> students;

    public Grade_Tracker_GUI(){
        students = new ArrayList<>();

        // Frame setup
        setTitle("Student Grade Tracker");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        //Input Panel
        JFrame fm = new JFrame();

        JLabel label = new JLabel("Student Grade Tracker");
        label.setFont(new Font("Arial", Font.BOLD, 25));
        label.setBounds(200,50,500,25);
        fm.add(label);
        JLabel l1 = new JLabel("Student Name: ");
        l1.setBounds(50,100,100,25);
        fm.add(l1);
        nameField = new JTextField();
        nameField.setBounds(150,103,150,25);
        fm.add(nameField);
        JLabel l2 = new JLabel("Total Marks: ");
        l2.setBounds(400,100,100,25);
        fm.add(l2);
        JTextField totalField = new JTextField();
        totalField.setBounds(500,100,100,25);
        fm.add(totalField);

        fm.setSize(750,750);
        fm.setLayout(null);
        fm.setVisible(true);


        // Buttons
        addButton = new JButton("Add Student");
        reportButton = new JButton("Show Report");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(reportButton);

        // Table
        tableModel = new DefaultTableModel(new String[]{"Name", "Grade"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Button actions
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                int grade;
                try{
                    grade = Integer.parseInt(gradeField.getText());
                } catch (NumberFormatException ex){
                    JOptionPane.showMessageDialog(null, "Please Enter a valid grade!");
                    return;
                }
                students.add(new student(name, grade));
                tableModel.addRow(new Object[]{name, grade});
                nameField.setText("");
                gradeField.setText("");
            }
        });
        reportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(students.isEmpty()){
                    JOptionPane.showMessageDialog(null, "No Student added!");
                    return;
                }
                int sum = 0, highest = Integer.MIN_VALUE, lowest = Integer.MAX_VALUE;
                String topStudent = "", lowStudent = "";

                for(student s : students){
                    sum += s.grade;
                    if (s.grade > highest){
                        highest = s.grade;
                        topStudent = s.name;
                    }
                    if (s.grade < lowest){
                        lowest = s.grade;
                        lowStudent = s.name;
                    }
                }

                double avarage = (double)sum/students.size();

                JOptionPane.showMessageDialog(null,
                        "Avarage Score: "+avarage+
                        "\nLowest Score: "+highest+
                        " (by "+topStudent+")"+
                        "\nLowest Score: "+lowest+" (by "+lowStudent+")");
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Grade_Tracker_GUI().setVisible(true);
        });
    }
}
