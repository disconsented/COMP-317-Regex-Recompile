public class Main {
    public static void main(String[] args) {
        if (args.length > 0) {
            Compiler c = new Compiler(args[0]);
            try {
                c.parse();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            String[] patterns = {"\\+(\\a\\ba-Z)?\\c\\d\\e\\fg", "[aaabbzz]", "(a*b+ac).+(a*b+ac)", "(a*b+ac)d", "ba*(a+b)b"};
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

class Compiler {
    private char[] p;
    private int j = 0;
    private int state = 0;
//    private static final char br = '\17';

    private char[] ch;
    private int[] next1;
    private int[] next2;
    private char[] notVocabs = {
            '(', ')',
            '[', ']',
            '*',//0 or more
            '+',//1 or more
            '?',//0 or 1 times
            '|',//Or
            '!',//Not
            '\\',//Escape
            '.'//Anything
    };

    Compiler(String pattern) {
        this.p = pattern.toCharArray();
        int patternLength = p.length*2;
        ch = new char[patternLength];
        next1 = new int[patternLength];
        next2 = new int[patternLength];
    }

    void parse() throws Exception {
        int initial = expression();
        //If there is anything left
        if (j > p.length)
            throw new Exception("End of Pattern");
        set_state(state, ' ', 0, 0);
        for (int i = 0; i < state + 1; i++) {
//            System.out.println(String.format("%s | %s %s %s", i, ch[i], next1[i], next2[i]));
            System.out.println(String.format("%s,%s,%s,%s", i, ch[i], next1[i], next2[i]));
        }
    }


    private int expression() throws Exception {
        int r;

        r = term();
        if (j >= p.length) {
            return r;
        } else if (isVocab(p[j]) || p[j] == '(' || p[j] == '[')
            expression();
        return (r);
    }

    private int term() throws Exception {
        int r, t1, t2, f;
        f = state - 1;
        r = t1 = factor();
        if (j >= p.length)
            return r;

        if (p[j] == '\\') {//Print and skip forward
            j++;
            set_state(state, p[j], state+1, state+1);
            j++;
            state++;
            r = term();
        } else if (p[j] == '*') {
            set_state(state, ' ', state + 1, t1);
            r = state;
            j++;
            state++;
        } else if (p[j] == '+') {
            if (next1[f] == next2[f])
                next2[f] = state;
            next1[f] = state;
            f = state - 1;
            j++;
            r = state;
            state++;
            t2 = term();
            set_state(r, ' ', t1, t2);
            if (next1[f] == next2[f])
                next2[f] = state;
            next1[f] = state;
        } else if (p[j] == '?') {
            set_state(state, ' ', state + 1, t1);
            r = state;
            j++;
            state++;
        } else if (p[j] == '.') {
            set_state(state, ' ', state + 1, state + 1);
            r = state;
            j++;
            state++;
        }
        return r;
    }

    private int factor() throws Exception {
        int r;

        if (j >= p.length)
            return state + 1;
        if (p[j] == '.') {
            set_state(state, ' ', state+1, state+1);
            r = state;
            j++;
            state++;
        } else if (isVocab(p[j])) {
            set_state(state, p[j], state + 1, state + 1);
            j++;
            r = state;
            state++;
        } else if (p[j] == '(') {
            j++;
            r = expression();
            if (p[j] == ')')
                j++;
            else
                throw new Exception("Unbalanced brackets");
        } else if (p[j] == '[') {
            int start = state;
            //Consume the token
            j++;
            //Position in the pattern of the ]
            int end = 0;
            //Scan forward for the entries in the literal
            for (int i = j; i < p.length; i++) {
                if (p[i] == ']' && p[i - 1] != '\\') {//Reached the end, break if we need to. Ensure its not escaped
                    end = i;
                    break;
                }

            }
            int length = end - j;

            //Output the branching states before the matches
            for (int i = state+1; i < state + length; i+=2) {
                set_state(state, ' ', i, i+1);
                state++;
            }

            //What the end state should be for the literal output
            int sequenceEnd = start + length * 2 -1;
            //Go through again this time outputting with the final state from this sequence
            for (int i = 0; i < length; i++) {
                set_state(state, p[j], sequenceEnd, sequenceEnd);
                state++;
                j++;
            }
            j++;
            r = state;
        } else if (p[j] == '!') {//Very similar to before but with a change to the output
            j++;
            if(p[j] != '['){
                throw new Exception("Expecting [ after ! instead got " + p[j]);
            }
            //Consume the token(2)
            j++;
            //Position in the pattern of the ]
            int end = 0;
            //Scan forward for the entries in the literal
            for (int i = j; i < p.length; i++) {
                if (p[i] == ']' && p[i] + 1 == '!' && p[i - 1] != '\\') {//Reached the end, break if we need to. Ensure its not escaped
                    end = i;
                    break;
                }

            }
            int length = end - j;

            //Output the branching states before the matches
            for (int i = state+1; i < state + length; i+=2) {
                set_state(state, ' ', i, i+1);
                state++;
            }

            //Go through again this time outputting with the final state from this sequence
            for (int i = 0; i < length; i++) {
                set_state(state, p[j], -1, -1);//-1 meaning don't match
                state++;
                j++;
            }
            j++;
            r = state;
        } else
            throw new Exception();
        return (r);
    }

    private void set_state(int s, char c, int n1, int n2) {
        ch[s] = c;
        next1[s] = n1;
        next2[s] = n2;
    }

    private boolean isVocab(char c) {
        for (char notVocab : notVocabs) {
            if (c == notVocab)
                return false;
        }
        return true;
    }
}
