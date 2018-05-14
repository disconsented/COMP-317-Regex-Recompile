public class Compiler {
    private char[] p;
    private int j = 0;
    private int state = 0;
    private static final char br = '\17';

    private char[] ch;
    private int[] next1;
    private int[] next2;
    private char[] notVocabs = {'(', ')', '[', ']', '*', '+'};

    Compiler(String pattern) {
        this.p = pattern.toCharArray();
        int patternLength = p.length;
        ch = new char[patternLength];
        next1 = new int[patternLength];
        next2 = new int[patternLength];
    }

    public void parse() throws Exception {
        int initial = expression();
        //If there is anything left
        if (j >= p.length || p[j] != 0)
            throw new Exception("End of Pattern");
        //I assume reset for later use
        set_state(initial, ' ', 0, 0);
    }

    private int expression() throws Exception {
        int r;
        r = term();
        //if (p[j] != 0) //Might be '\0'
        if (j < p.length && (isVocab(p[j]) || p[j] == '('))
            expression();

        return r;
    }

    private int term() throws Exception {
        int r, t1, t2, f;
        f = state - 1;
        r = t1 = factor();
        if (j < p.length && p[j] == '*') {
            set_state(state, br, state+1, t1);
            r = state;
            j++;
            state++;
        } else if (j < p.length && p[j] == '+') {
            if(next1[f]==next2[f])
                next2[f]=state;
            next1[f]=state;
            f=state-1;
            j++;r=state;state++;
            t2=term();
            set_state(r,' ',t1,t2);
            if(next1[f]==next2[f])
                next2[f]=state;
            next1[f]=state;
        }
        return r;
    }

    private int factor() throws Exception {
        int r;
        if (isVocab(p[j])) {
            set_state(state, p[j], state + 1, state + 1);
            r = state;
            state++;
            j++;
            return state - 1;
        } else if (p[j] == '(') {
            j++;
            r = expression();
            if (p[j] != ')') {
                throw new Exception("Not balanced brackets");
            } else {
                j++;
            }
        } else {
            throw new Exception("No where to go but up");
        }
        return r;
    }

    private void set_state(int s, char c, int n1, int n2) {
        ch[s] = c;
        next1[s] = n1;
        next2[s] = n2;
        System.out.println(String.format("%s,%s,%s,%s", s, c, n1, n2));
    }

    private boolean isVocab(char c) {
        for (char notVocab : notVocabs) {
            if (c == notVocab)
                return false;
        }
        return true;
    }
}
