import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class SchedulingAlgorithmsGUI {
    private ArrayList<Process> processes = new ArrayList<>();
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField numProcessesField;
    private JComboBox<String> algorithmComboBox;
    private JButton runButton;
    private JPanel ganttChartPanel;

    public SchedulingAlgorithmsGUI() {
        JFrame frame = new JFrame("Scheduling Algorithms");
        frame.setSize(1000, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Toolkit toolKit = frame.getToolkit();
        Dimension size = toolKit.getScreenSize();
        frame.setLocation(size.width / 2 - frame.getWidth() / 2, size.height / 2 - frame.getHeight() / 2);
        frame.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        numProcessesField = new JTextField(10);
        numProcessesField.setToolTipText("Enter the number of processes");
        inputPanel.add(new JLabel("Number of Processes:"));
        inputPanel.add(numProcessesField);

        String[] algorithms = {"FCFS", "SJF", "Round Robin", "Priority Scheduling"};
        algorithmComboBox = new JComboBox<>(algorithms);
        algorithmComboBox.setToolTipText("Choose a scheduling algorithm");
        inputPanel.add(new JLabel("Choose Algorithm:"));
        inputPanel.add(algorithmComboBox);

        runButton = new JButton("Run Algorithm");
        runButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                runAlgorithm();
            }
        });
        inputPanel.add(runButton);

        frame.add(inputPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        tableModel.addColumn("Process");
        tableModel.addColumn("Arrival Time");
        tableModel.addColumn("Burst Time");
        tableModel.addColumn("Priority");
        tableModel.addColumn("Completion Time");
        tableModel.addColumn("Turnaround Time");
        tableModel.addColumn("Waiting Time");

        JScrollPane tableScrollPane = new JScrollPane(table);
        frame.add(tableScrollPane, BorderLayout.CENTER);

        ganttChartPanel = new JPanel();
        frame.add(ganttChartPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void runAlgorithm() {
        processes.clear();
        tableModel.setRowCount(0);
        ganttChartPanel.removeAll();
    
        int numProcesses = Integer.parseInt(numProcessesField.getText());
        for (int i = 0; i < numProcesses; i++) {
            JTextField arrivalTimeField = new JTextField(10);
            JTextField burstTimeField = new JTextField(10);
            JTextField priorityField = new JTextField(10);
    
            JPanel inputPanel = new JPanel();
            inputPanel.setLayout(new GridLayout(3, 2));
            inputPanel.add(new JLabel("Arrival Time for Process " + (i + 1) + ":"));
            inputPanel.add(arrivalTimeField);
            inputPanel.add(new JLabel("Burst Time for Process " + (i + 1) + ":"));
            inputPanel.add(burstTimeField);
            inputPanel.add(new JLabel("Priority for Process " + (i + 1) + ":"));
            inputPanel.add(priorityField);
    
            int result = JOptionPane.showConfirmDialog(null, inputPanel, "Enter Process Details", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                int arrivalTime = Integer.parseInt(arrivalTimeField.getText());
                int burstTime = Integer.parseInt(burstTimeField.getText());
                int priority = Integer.parseInt(priorityField.getText());
                processes.add(new Process(i + 1, arrivalTime, burstTime, priority));
            }
        }
    
        int choice = algorithmComboBox.getSelectedIndex();
        switch (choice) {
            case 0:
                SchedulingAlgorithms.FCFS(processes);
                break;
            case 1:
                SchedulingAlgorithms.SJF(processes);
                break;
            case 2:
                int timeQuantum = Integer.parseInt(JOptionPane.showInputDialog("Enter the time quantum for Round Robin:"));
                SchedulingAlgorithms.roundRobin(processes, timeQuantum);
                break;
            case 3:
                SchedulingAlgorithms.priorityScheduling(processes);
                break;
            default:
                break;
        }
    
        ArrayList<Process> executedProcesses = new ArrayList<>();
        for (Process process : processes) {
            if (process.completionTime > 0) {
                executedProcesses.add(process);
            }
        }
    
        // Sort executed processes based on their completion time
        executedProcesses.sort((a, b) -> Integer.compare(a.completionTime, b.completionTime));
    
        // Add Gantt chart representation based on the order of execution
        int previousCompletionTime = 0;
        for (Process process : executedProcesses) {
            JLabel processLabel = new JLabel("P" + process.id);
            processLabel.setHorizontalAlignment(SwingConstants.CENTER);
            processLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            int burstTime = process.completionTime - previousCompletionTime;
            processLabel.setPreferredSize(new Dimension(burstTime * 10, 40));
            processLabel.setOpaque(true);
            processLabel.setBackground(Color.CYAN);
            processLabel.setForeground(Color.BLACK);
            processLabel.setToolTipText("Start Time: " + previousCompletionTime + " End Time: " + process.completionTime);
    
            ganttChartPanel.add(processLabel);
    
            previousCompletionTime = process.completionTime;
        }
    
        // Repaint the panel
        ganttChartPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); // Centered layout
        ganttChartPanel.setPreferredSize(new Dimension(800, 40)); // Adjust the panel's size
        ganttChartPanel.revalidate();
        ganttChartPanel.repaint();
    
        for (Process process : processes) {
            tableModel.addRow(new Object[]{
                process.id,
                process.arrivalTime,
                process.burstTime,
                process.priority,
                process.completionTime,
                process.turnaroundTime,
                process.waitingTime
            });
        }
    }
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new SchedulingAlgorithmsGUI();
            }
        });
    }
}
