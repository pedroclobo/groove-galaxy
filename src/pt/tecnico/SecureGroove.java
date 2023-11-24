package pt.tecnico;

public class SecureGroove {
    public static void main(String[] args) {
        if (args.length == 0) {
            printHelp();
            return;
        }

        String commandKind = args[0];
        switch (commandKind) {
            case "protect":
                throw new UnsupportedOperationException("Not implemented yet");

            case "check":
                throw new UnsupportedOperationException("Not implemented yet");

            case "unprotect":
                throw new UnsupportedOperationException("Not implemented yet");

            default:
                printHelp();
                break;
        }
    }

    private static void printHelp() {
        System.err.println("Argument(s) missing!");
        System.err.println("Usage: secure-groove help");
        System.err.println("Usage: secure-groove protect (input-file) (output-file) (key-file)");
        System.err.println("Usage: secure-groove check (input-file) (key-file)");
        System.err.println("Usage: secure-groove unprotect (input-file) (output-file) (key-file)");
    }

    private static void protect(String input, String output, String key) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private static void check(String input, String key) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private static void unprotect(String input, String output, String key) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
