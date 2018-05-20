public class Main {
    public static void main(String[] args) {
        if(args.length > 0){

        } else {
            String[] patterns = { "\\+(\\a\\ba-Z)?\\c\\d\\e\\fg", "[aaabbzz]", "(a*b+ac).+(a*b+ac)", "(a*b+ac)d", "ba*(a+b)b"};
            for (String pattern : patterns) {
                Compiler c = new Compiler(pattern);
                try {
                    System.out.println("\ns  ch 1 2");
                    System.out.println("--+--+-+-+");
                    c.parse();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
