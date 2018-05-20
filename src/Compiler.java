public class Compiler {
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
        int patternLength = p.length;
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
        for (int i = 0; i < state+1; i++) {
            System.out.println(String.format("%s | %s %s %s", i, ch[i], next1[i], next2[i]));
        }
    }


    private int expression() throws Exception {
        int r;

        r = term();
        if(j >= p.length){
            return r;
        } else if (isVocab(p[j]) || p[j] == '(' || p[j] == '[')
            expression();
        return (r);
    }

    private int term() throws Exception {
        int r,t1,t2,f;
        f = state-1;
        r = t1 = factor();
        if(j >= p.length)
            return r;

         if (p[j] == '*'){
            set_state(state,' ',state+1,t1);
            r = state;
            j++;
            state++;
        } else if(p[j] == '+'){
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
        } else if(p[j] == '?'){
             set_state(state,' ',state+1,t1);
             r = state;
             j++;
             state++;
         } else if(p[j] == '.'){
             set_state(state,' ',state+1,state+1);
             r = state;
             j++;
             state++;
         }
        return r;
    }

    private int factor() throws Exception {
        int r;

        if(j >= p.length)
            return state+1;
        if(p[j] == '\\'){//Print and skip forward
            set_state(state, p[j+1], state + 1, state + 1);
            j+=2;
            r = state;
            state++;
            r = factor();
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
        } else if(p[j] == '['){
            j++;
            int t1 = j;
            for (int i = j; i < p.length; i++) {
                if(!isVocab(p[i]) && p[i-1] != '\\'){
                    if(p[i] == ']'){
                        set_state(state, ' ', t1, i);
                        j = i;
                        state++;
                        return t1;
                    }
                }
            }
            throw new Exception("Unbalanced parenthesis");
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
