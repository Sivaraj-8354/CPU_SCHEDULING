import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

class Process implements Comparable<Process> {
    int id;
    int arrivalTime;
    int burstTime;  // Original burst time
    int remainingBurstTime; // Remaining burst time
    int priority; // Priority value (lower value indicates higher priority)
    int completionTime;
    int turnaroundTime;
    int waitingTime;

    public Process(int _id, int _arrivalTime, int _burstTime, int _priority) {
        id = _id;
        arrivalTime = _arrivalTime;
        burstTime = _burstTime;
        remainingBurstTime = _burstTime;
        priority = _priority;
        completionTime = 0;
        turnaroundTime = 0;
        waitingTime = 0;
    }

    @Override
    public int compareTo(Process other) {
        // Priority scheduling: Lower priority value means higher priority
        if (priority != other.priority) {
            return Integer.compare(priority, other.priority);
        }
        // If priorities are equal, use arrival time as a tie-breaker
        return Integer.compare(arrivalTime, other.arrivalTime);
    }
}

public class SchedulingAlgorithms {
    public static void FCFS(ArrayList<Process> processes) {
        int currentTime = 0;
        processes.sort((a, b) -> Integer.compare(a.arrivalTime, b.arrivalTime));

        for (Process process : processes) {
            if (process.arrivalTime > currentTime) {
                currentTime = process.arrivalTime;
            }
            process.completionTime = currentTime + process.remainingBurstTime;
            process.turnaroundTime = process.completionTime - process.arrivalTime;
            process.waitingTime = process.turnaroundTime - process.burstTime;
            currentTime = process.completionTime;
        }
    }

    public static void SJF(ArrayList<Process> processes) {
        int currentTime = 0;
        int completedProcesses = 0;
    
        while (completedProcesses < processes.size()) {
            int shortestBurstTime = Integer.MAX_VALUE;
            int shortestProcessIndex = -1;
    
            for (int i = 0; i < processes.size(); i++) {
                Process process = processes.get(i);
                if (process.arrivalTime <= currentTime && process.remainingBurstTime > 0) {
                    if (process.remainingBurstTime < shortestBurstTime) {
                        shortestBurstTime = process.remainingBurstTime;
                        shortestProcessIndex = i;
                    }
                }
            }
    
            if (shortestProcessIndex != -1) {
                Process currentProcess = processes.get(shortestProcessIndex);
                currentProcess.remainingBurstTime--;
                currentTime++;
    
                if (currentProcess.remainingBurstTime == 0) {
                    currentProcess.completionTime = currentTime;
                    currentProcess.turnaroundTime = currentProcess.completionTime - currentProcess.arrivalTime;
                    currentProcess.waitingTime = currentProcess.turnaroundTime - currentProcess.burstTime;
                    completedProcesses++;
                }
            } else {
                // If no process is available to run, just increment time
                currentTime++;
            }
        }
    }
    
    
    public static void roundRobin(ArrayList<Process> processes, int timeQuantum) {
        int currentTime = 0;
        Queue<Process> readyQueue = new LinkedList<>();
        ArrayList<Process> completedProcesses = new ArrayList<>();

        while (!processes.isEmpty() || !readyQueue.isEmpty()) {
            while (!processes.isEmpty() && processes.get(0).arrivalTime <= currentTime) {
                readyQueue.add(processes.get(0));
                processes.remove(0);
            }

            if (!readyQueue.isEmpty()) {
                Process currentProcess = readyQueue.poll();
                int executionTime = Math.min(timeQuantum, currentProcess.remainingBurstTime);
                currentTime += executionTime;
                currentProcess.remainingBurstTime -= executionTime;

                if (currentProcess.remainingBurstTime > 0) {
                    readyQueue.add(currentProcess);
                } else {
                    currentProcess.completionTime = currentTime;
                    currentProcess.turnaroundTime = currentProcess.completionTime - currentProcess.arrivalTime;
                    currentProcess.waitingTime = currentProcess.turnaroundTime - currentProcess.burstTime;
                    completedProcesses.add(currentProcess);
                }
            } else {
                currentTime++;
            }
        }

        processes.addAll(completedProcesses);
    }

    public static void priorityScheduling(ArrayList<Process> processes) {
    int currentTime = 0;
    ArrayList<Process> readyQueue = new ArrayList<>();
    ArrayList<Process> completedProcesses = new ArrayList<>();

    while (!processes.isEmpty() || !readyQueue.isEmpty()) {
        while (!processes.isEmpty() && processes.get(0).arrivalTime <= currentTime) {
            readyQueue.add(processes.get(0));
            processes.remove(0);
        }

        if (!readyQueue.isEmpty()) {
            int highestPriorityIndex = -1;
            for (int i = 0; i < readyQueue.size(); i++) {
                if (highestPriorityIndex == -1 || (readyQueue.get(i).priority < readyQueue.get(highestPriorityIndex).priority) ||
                        (readyQueue.get(i).priority == readyQueue.get(highestPriorityIndex).priority && readyQueue.get(i).arrivalTime < readyQueue.get(highestPriorityIndex).arrivalTime)) {
                    highestPriorityIndex = i;
                }
            }

            Process currentProcess = readyQueue.get(highestPriorityIndex);
            readyQueue.remove(highestPriorityIndex);

            // Update completion time based on remaining burst time
            int executionTime = Math.min(1, currentProcess.remainingBurstTime);
            currentTime += executionTime;
            currentProcess.remainingBurstTime -= executionTime;

            if (currentProcess.remainingBurstTime == 0) {
                currentProcess.completionTime = currentTime;
                currentProcess.turnaroundTime = currentProcess.completionTime - currentProcess.arrivalTime;
                currentProcess.waitingTime = currentProcess.turnaroundTime - currentProcess.burstTime;
                completedProcesses.add(currentProcess);
            } else {
                // If the process is not completed, add it back to the ready queue
                readyQueue.add(currentProcess);
            }
        } else {
            currentTime++;
        }
    }

    processes.addAll(completedProcesses);
}
}
