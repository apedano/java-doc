public class ProcessHandleFetaureExample {

    /*
        Process ID: 14184
        Is alive? true
     */
    public static void main(String[] args) {
        ProcessHandle currentProcess = ProcessHandle.current();
        System.out.println("Process ID: " + currentProcess.pid());
        System.out.println("Is alive? " + currentProcess.isAlive());
    }
}
