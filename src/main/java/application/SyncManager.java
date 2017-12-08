package application;

import commons.*;
import console.SwingConsole;
import logger.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

/** Main application window */

public class SyncManager extends JFrame {

    private Vector<Vector<String>> taskTableData;
    private Logger logger = AppLogger.getInstance();

    private TaskController taskController = SharedTaskController.getInstance();
    private TaskTableController tableController = SharedTaskController.getInstance();

    private JScrollPane taskTablePane;
    private JTable taskTable;
    private SwingConsole swingConsole;

    private JButton addNewButton;
    private JButton removeButton;
    private JButton editButton;
    private JButton runButton;
    private JButton stopButton;

    private JButton syncAllButton;
    private JButton startAllButton;
    private JButton stopAllButton;

    private int resolutionX;
    private int resolutionY;


    /** Main window constructor */

    private SyncManager() {
        super("Sync Manager v0.9 beta");
        taskTableData = tableController.getTaskTable();
        taskController.setLogger(this.logger);
        createWindow();
    }

    /** Initializer for main window elements */

    private void createWindow() {

        this.resolutionX = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        this.resolutionY = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();

        this.setSize(1000, 720);
        this.setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(null);
        this.setLocation((resolutionX  - 1000) / 2,
                (resolutionY - 720 ) / 2);
        this.setVisible(true);

        addNewButton = new JButton("Add New");
        addNewButton.setBounds(50, 445, 80, 30);
        addNewButton.addActionListener(new AddNewButtonListener());
        this.add(addNewButton);

        editButton = new JButton("Edit");
        editButton.setBounds(140, 445, 80, 30);
        editButton.addActionListener(new EditButtonListener());
        this.add(editButton);

        removeButton = new JButton("Remove");
        removeButton.setBounds(230, 445, 80, 30);
        removeButton.addActionListener(new RemoveButtonListener());
        this.add(removeButton);

        runButton = new JButton("Run");
        runButton.setBounds(320, 445, 80, 30);
        runButton.addActionListener(new RunButtonListener());
        this.add(runButton);

        stopButton = new JButton("Stop");
        stopButton.setBounds(410, 445, 80, 30);
        stopButton.addActionListener(new StopButtonListener());
        this.add(stopButton);


        syncAllButton = new JButton("Synchronize All");
        syncAllButton.setBounds(540, 440, 130, 36);
        syncAllButton.addActionListener(new SyncAllButtonListener());
        this.add(syncAllButton);

        startAllButton = new JButton("Start All");
        startAllButton.setBounds(680, 440, 130, 36);
        startAllButton.addActionListener(new StartAllButtonListener());
        this.add(startAllButton);

        stopAllButton = new JButton("Stop All");
        stopAllButton.setBounds(820, 440, 130, 36);
        stopAllButton.addActionListener(new StopAllButtonListener());
        this.add(stopAllButton);

        swingConsole = SwingConsole.getInstance();
        swingConsole.setBounds(20, 520, 960, 150);
        this.add(swingConsole);

        taskTable = prepareTaskTable();
        taskTablePane = new JScrollPane(taskTable);
        taskTablePane.setBounds(20, 50, 960, 370);
        this.add(taskTablePane);

        this.repaint(0, 0, 1000, 720);
    }

    protected void refreshTaskTable() {
        taskTable = prepareTaskTable();
        taskTablePane.setViewportView(taskTable);
    }

    private JTable prepareTaskTable() {

        Vector<String> header = new Vector<>();
        header.add("Source Directory");
        header.add("Destination Directory");
        header.add("Last Sync Date");
        header.add("Task Status");
        header.add("Sync Type");
        header.add("Sync Schedule");
        header.add("Running Status");

        taskTableData = tableController.getTaskTable();

        DefaultTableModel dtm = new DefaultTableModel(taskTableData, header) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };

        JTable taskTable = new JTable(dtm);
        TableRenderer renderer = new TableRenderer(taskTableData);

        taskTable.setDefaultRenderer(Object.class, renderer);
        taskTable.setRowHeight(36);
        taskTable.getSelectionModel().setSelectionMode(0);
        taskTable.getColumnModel().getColumn(0).setPreferredWidth(250);
        taskTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        taskTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        taskTable.getColumnModel().getColumn(3).setPreferredWidth(70);
        taskTable.getColumnModel().getColumn(4).setPreferredWidth(70);
        taskTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        taskTable.getColumnModel().getColumn(6).setPreferredWidth(100);

        return taskTable;
    }

    private void enableButtons(boolean enable) {
        editButton.setEnabled(enable);
        removeButton.setEnabled(enable);
        syncAllButton.setEnabled(enable);
        startAllButton.setEnabled(enable);
    }


    /** Main window button listeners */

    private class AddNewButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            new AddTaskWindow();
        }
    }

    private class EditButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (taskTable.getSelectedRow() < 0) {
                JOptionPane.showMessageDialog(null, "Please select task to edit");
            }
            else {
                int taskNum = taskTable.getSelectedRow();
                Task editedTask = taskController.getTask(taskNum);
                new AddTaskWindow(editedTask, taskTable.getSelectedRow());
            }
        }
    }

    private class RemoveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (taskTable.getSelectedRow() < 0) {
                JOptionPane.showMessageDialog(null, "Please select task to remove");
            }
            else
            if (JOptionPane.showConfirmDialog(null, "Remove selected task?") == JOptionPane.OK_OPTION) {
                taskController.deleteTask(taskTable.getSelectedRow());
                refreshTaskTable();
            }
        }
    }

    private class RunButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (taskTable.getSelectedRow() < 0) {
                JOptionPane.showMessageDialog(null, "Please select task to run");
            }
            else {
                int taskNum = taskTable.getSelectedRow();
                taskController.startTask(taskNum);
            }

            refreshTaskTable();
        }
    }

    private class StopButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (taskTable.getSelectedRow() < 0) {
                JOptionPane.showMessageDialog(null, "Please select task to stop");
            }
            else {
                int taskNum = taskTable.getSelectedRow();
                taskController.stopTask(taskNum);
                enableButtons(true);
            }

            refreshTaskTable();
        }
    }

    private class SyncAllButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            taskController.syncAll();
            refreshTaskTable();
        }
    }

    private class StartAllButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            enableButtons(false);
            taskController.startAll();
            refreshTaskTable();
        }
    }

    private class StopAllButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            enableButtons(true);
            taskController.stopAll();
            refreshTaskTable();
        }
    }


    /** Window used for adding new task or editing existing task */

    private class AddTaskWindow extends JFrame {

        private String sourceDirectory;
        private String destinationDirectory;

        private JCheckBox isTaskSchedule;
        private JCheckBox isTaskInterval;

        private JCheckBox deleteFromDestination;

        private JLabel sourceLabel;
        private JLabel destinationLabel;

        private JLabel taskScheduleLabel_H;
        private JLabel taskScheduleLabel_M;
        private JLabel taskScheduleLabel_S;

        private JLabel taskIntervalLabel_H;
        private JLabel taskIntervalLabel_M;
        private JLabel taskIntervalLabel_S;

        private JTextField sourceDirectoryField;
        private JTextField destinationDirectoryField;

        private JTextField taskScheduleField_H = new JFormattedTextField();
        private JTextField taskScheduleField_M = new JFormattedTextField();
        private JTextField taskScheduleField_S = new JFormattedTextField();

        private JTextField taskIntervalField_H = new JFormattedTextField();
        private JTextField taskIntervalField_M = new JFormattedTextField();
        private JTextField taskIntervalField_S = new JFormattedTextField();

        private JButton sourceBrowseBtn;
        private JButton destinationBrowseBtn;
        private JButton okButton;
        private JButton cancelButton;

        private SBrowseBtnListener sBrowseBtnListener = new SBrowseBtnListener();
        private DBrowseBtnListener dBrowseBtnListener = new DBrowseBtnListener();
        private OkBtnListener okBtnListener = new OkBtnListener();
        private CancelBtnListener cancelBrowseBtnListener = new CancelBtnListener();

        private int taskNum;

        private boolean isNew;

        AddTaskWindow() {

            super("Add new task");

            this.setSize(540, 360);
            this.setLocation(resolutionX / 2 - 270, resolutionY / 2 - 180);
            this.setDefaultCloseOperation(HIDE_ON_CLOSE);
            this.setLayout(null);
            this.isNew = true;

            sourceLabel = new JLabel("Source Directory:");
            sourceLabel.setBounds(20, 10, 350, 16);
            this.add(sourceLabel);

            sourceDirectoryField = new JFormattedTextField();
            sourceDirectoryField.setEditable(false);
            sourceDirectoryField.setBounds(20, 30, 360, 25);
            this.add(sourceDirectoryField);

            destinationLabel = new JLabel("Destination Directory:");
            destinationLabel.setBounds(20, 70, 350, 16);
            this.add(destinationLabel);

            destinationDirectoryField = new JFormattedTextField();
            destinationDirectoryField.setEditable(false);
            destinationDirectoryField.setBounds(20, 90, 360, 25);
            this.add(destinationDirectoryField);

            sourceBrowseBtn = new JButton("Browse");
            sourceBrowseBtn.setBounds(400, 30, 90, 25);
            sourceBrowseBtn.addActionListener(sBrowseBtnListener);
            this.add(sourceBrowseBtn);

            destinationBrowseBtn = new JButton("Browse");
            destinationBrowseBtn.setBounds(400, 90, 90, 25);
            destinationBrowseBtn.addActionListener(dBrowseBtnListener);
            this.add(destinationBrowseBtn);

            deleteFromDestination = new JCheckBox("Delete files from destination directory if it still not present in the source");
            deleteFromDestination.setBounds(20, 130, 960, 25);
            this.add(deleteFromDestination);

            isTaskSchedule = new JCheckBox("Use time schedule:");
            isTaskSchedule.setBounds(50, 180, 150, 20);
            isTaskSchedule.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    taskScheduleField_H.setEditable(isTaskSchedule.isSelected());
                    taskScheduleField_M.setEditable(isTaskSchedule.isSelected());
                    taskScheduleField_S.setEditable(isTaskSchedule.isSelected());
                }
            });
            this.add(isTaskSchedule);

            taskScheduleLabel_H = new JLabel("Hours:");
            taskScheduleLabel_H.setBounds(235, 180, 40, 20);
            this.add(taskScheduleLabel_H);
            taskScheduleLabel_M = new JLabel("Mins:");
            taskScheduleLabel_M.setBounds(325, 180, 40, 20);
            this.add(taskScheduleLabel_M);
            taskScheduleLabel_S = new JLabel("Secs:");
            taskScheduleLabel_S.setBounds(405, 180, 40, 20);
            this.add(taskScheduleLabel_S);

            taskScheduleField_H.setBounds(285, 180, 25, 20);
            taskScheduleField_H.setEditable(isTaskSchedule.isSelected());
            this.add(taskScheduleField_H);
            taskScheduleField_M.setBounds(365, 180, 25, 20);
            taskScheduleField_M.setEditable(isTaskSchedule.isSelected());
            this.add(taskScheduleField_M);
            taskScheduleField_S.setBounds(445, 180, 25, 20);
            taskScheduleField_S.setEditable(isTaskSchedule.isSelected());
            this.add(taskScheduleField_S);

            isTaskInterval = new JCheckBox("Use time interval:");
            isTaskInterval.setBounds(50, 220, 150, 20);
            isTaskInterval.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    taskIntervalField_H.setEditable(isTaskInterval.isSelected());
                    taskIntervalField_M.setEditable(isTaskInterval.isSelected());
                    taskIntervalField_S.setEditable(isTaskInterval.isSelected());
                }
            });
            this.add(isTaskInterval);

            taskIntervalLabel_H = new JLabel("Hours:");
            taskIntervalLabel_H.setBounds(235, 220, 40, 20);
            this.add(taskIntervalLabel_H);
            taskIntervalLabel_M = new JLabel("Mins:");
            taskIntervalLabel_M.setBounds(325, 220, 40, 20);
            this.add(taskIntervalLabel_M);
            taskIntervalLabel_S = new JLabel("Secs:");
            taskIntervalLabel_S.setBounds(405, 220, 40, 20);
            this.add(taskIntervalLabel_S);

            taskIntervalField_H.setBounds(285, 220, 25, 20);
            taskIntervalField_H.setEditable(isTaskInterval.isSelected());
            this.add(taskIntervalField_H);
            taskIntervalField_M.setBounds(365, 220, 25, 20);
            taskIntervalField_M.setEditable(isTaskInterval.isSelected());
            this.add(taskIntervalField_M);
            taskIntervalField_S.setBounds(445, 220, 25, 20);
            taskIntervalField_S.setEditable(isTaskInterval.isSelected());
            this.add(taskIntervalField_S);

            okButton = new JButton("Apply");
            okButton.setBounds(170, 275, 80, 32);
            okButton.addActionListener(okBtnListener);
            this.add(okButton);

            cancelButton = new JButton("Cancel");
            cancelButton.setBounds(290, 275, 80, 32);
            cancelButton.addActionListener(cancelBrowseBtnListener);
            this.add(cancelButton);

            this.setVisible(true);
        }

        AddTaskWindow (Task task, int taskNum) {

            this();
            this.setTitle("Edit task");

            this.taskNum = taskNum;
            this.isNew = false;

            sourceDirectoryField.setText(task.getSourceDirectory());
            destinationDirectoryField.setText(task.getDestinationDirectory());

            deleteFromDestination.setSelected(task.isDeleteFromDestination());

            isTaskSchedule.setSelected(task.isSyncBySchedule());
            taskScheduleField_H.setText(task.getScheduleHours());
            taskScheduleField_M.setText(task.getScheduleMinutes());
            taskScheduleField_S.setText(task.getScheduleSeconds());

            isTaskInterval.setSelected(task.isSyncByInterval());
            taskIntervalField_H.setText(task.getIntervalHours());
            taskIntervalField_M.setText(task.getIntervalMinutes());
            taskIntervalField_S.setText(task.getIntervalSeconds());
        }

        private class SBrowseBtnListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser sourceDirChooser = new JFileChooser();
                sourceDirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = sourceDirChooser.showDialog(null, "Ok");
                if (result == JFileChooser.APPROVE_OPTION) {
                    sourceDirectory = sourceDirChooser.getSelectedFile().getAbsolutePath();
                    sourceDirectoryField.setText(sourceDirectory);
                }
            }
        }

        private class DBrowseBtnListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser destinationDirChooser = new JFileChooser();
                destinationDirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = destinationDirChooser.showDialog(null, "Ok");
                if (result == JFileChooser.APPROVE_OPTION) {
                    destinationDirectory = destinationDirChooser.getSelectedFile().getAbsolutePath();
                    destinationDirectoryField.setText(destinationDirectory);
                }
            }
        }

        private class OkBtnListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {

                sourceDirectory = sourceDirectoryField.getText();
                destinationDirectory = destinationDirectoryField.getText();

                if (sourceDirectory.isEmpty() && !destinationDirectory.isEmpty())
                    JOptionPane.showMessageDialog(null, "Please define source directory");
                if (destinationDirectory.isEmpty() && !sourceDirectory.isEmpty())
                    JOptionPane.showMessageDialog(null, "Please define destination directory");
                if (destinationDirectory.isEmpty() && sourceDirectory.isEmpty())
                    JOptionPane.showMessageDialog(null, "Please define source and destination directories");

                if ((!sourceDirectory.isEmpty()) && (!destinationDirectory.isEmpty())) {

                    Task newTask = new Task(sourceDirectory, destinationDirectory);

                    newTask.setDeleteFromDestination(deleteFromDestination.isSelected());
                    newTask.setSyncByInterval(isTaskInterval.isSelected());
                    newTask.setSyncBySchedule(isTaskSchedule.isSelected());

                    if (newTask.isSyncByInterval()) {
                        newTask.setIntervalHours(taskIntervalField_H.getText());
                        newTask.setIntervalMinutes(taskIntervalField_M.getText());
                        newTask.setIntervalSeconds(taskIntervalField_S.getText());
                    }

                    if (newTask.isSyncBySchedule()) {
                        newTask.setScheduleHours(taskScheduleField_H.getText());
                        newTask.setScheduleMinutes(taskScheduleField_M.getText());
                        newTask.setScheduleSeconds(taskScheduleField_S.getText());
                    }

                    newTask.create();
                    if (isNew) taskController.addNewTask(newTask);
                    else taskController.substituteTask(taskNum, newTask);
                    refreshTaskTable();

                    if (taskController.saveTaskList())
                        logger.logEvent("Data saved");
                    else
                        logger.logEvent("Error: data not saved");

                    AddTaskWindow.this.setVisible(false);
                }
            }
        }

        private class CancelBtnListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                sourceDirectory = null;
                destinationDirectory = null;
                refreshTaskTable();
                AddTaskWindow.this.setVisible(false);
            }
        }

    }

    public static void main(String... args) {
        new SyncManager();
    }
}