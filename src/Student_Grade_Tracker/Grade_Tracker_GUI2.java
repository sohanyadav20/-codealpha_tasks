package Student_Grade_Tracker;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

class Student {
    String name;
    int total;
    int obtained;
    String grade;
    Student(String name, int marks, String grade, int obtained) {
        this.name = name;
        this.obtained = obtained;
        this.total = marks;
        this.grade = grade;
    }
}

public class Grade_Tracker_GUI2 {
    private static ArrayList<Student> students = new ArrayList<>();
    private static DefaultTableModel tableModel;

    public static void main(String[] args) {
        JFrame fm = new JFrame("Student Grade Tracker");
        fm.setSize(800, 600);
        fm.setLayout(null);
        fm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Title Label
        JLabel title = new JLabel("Student Grade Tracker");
        title.setFont(new Font("Arial", Font.BOLD, 25));
        title.setBounds(250, 20, 400, 30);
        fm.add(title);

        // Name Label + TextField
        JLabel l1 = new JLabel("Student Name:");
        l1.setBounds(50, 80, 120, 25);
        fm.add(l1);

        JTextField nameField = new JTextField();
        nameField.setBounds(180, 80, 150, 25);
        fm.add(nameField);

        // Grade Label + TextField
        JLabel l2 = new JLabel("Obtained Marks:");
        l2.setBounds(400, 80, 100, 25);
        fm.add(l2);

        JTextField obtainedField = new JTextField();
        obtainedField.setBounds(520, 80, 100, 25);
        fm.add(obtainedField);

        // Total Marks
        JLabel l3 = new JLabel("Total Marks:");
        l3.setBounds(50, 120, 120, 25);
        fm.add(l3);

        JTextField totalField = new JTextField();
        totalField.setBounds(180, 120, 150, 25);
        fm.add(totalField);

        // Buttons
        JButton addButton = new JButton("Add Student");
        addButton.setBounds(200, 160, 150, 30);
        fm.add(addButton);

        JButton reportButton = new JButton("Show Report");
        reportButton.setBounds(400, 160, 150, 30);
        fm.add(reportButton);

        // Table
        tableModel = new DefaultTableModel(new String[]{"Name", "Obtained", "Total Marks", "Grade"}, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(50, 200, 700, 250);
        fm.add(scrollPane);

        // Add Student Action
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                int obtained,total;
                try {
                    obtained = Integer.parseInt(obtainedField.getText());
                    total = Integer.parseInt(totalField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(fm, "Please enter a valid grade!");
                    return;
                }

                // Calculate percentage and grade
                double percent = (double) obtained/total*100;
                String grade;
                if(percent >= 90) grade = "A+";
                else if (percent >= 75) grade = "A";
                else if (percent >= 60) grade = "B";
                else if (percent >= 45) grade = "C";
                else grade = "D";

                students.add(new Student(name, total,  grade, obtained));
                tableModel.addRow(new Object[]{name, obtained, total, grade});
                nameField.setText("");
                obtainedField.setText("");
                totalField.setText("");
            }
        });

        // Show Report Action
        reportButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (students.isEmpty()) {
                    JOptionPane.showMessageDialog(fm, "No students added yet!");
                    return;
                }

                int sum = 0, highest = Integer.MIN_VALUE, lowest = Integer.MAX_VALUE;
                String topStudent = "", lowStudent = "";

                for (Student s : students) {
                    sum += s.obtained;
                    if (s.obtained > highest) {
                        highest = s.obtained;
                        topStudent = s.name;
                    }
                    if (s.obtained < lowest) {
                        lowest = s.obtained;
                        lowStudent = s.name;
                    }
                }

                double average = (double) sum / students.size();

                JOptionPane.showMessageDialog(fm,
                        "Average Score: " + average +
                                "\nHighest Score: " + highest + " (by " + topStudent + ")" +
                                "\nLowest Score: " + lowest + " (by " + lowStudent + ")");
            }
        });

        fm.setVisible(true);
    }
}

