(
~r1 = Routine{
    6.do {
        ["выше","ниже"].choose.yield;
    };
};

~r2 = Routine{
    6.do {
        ["выше","ниже","короче","длиннее"].choose.yield;
    };
};

~r3 = Routine{
    6.do {
        ["выше","ниже","короче","длиннее","громче","тише"].choose.yield;
    };
};


~r4 = Routine{
    6.do {
        ["короче","длиннее","громче","тише"].choose.yield;
    };
};

~r5 = Routine{
    6.do {
        ["громче","тише"].choose.yield;
    };
};

~r0 = Routine {
    6.do {
        ~r1.next.speak;
        1.yield;
    };

    6.do {
        ~r2.next.speak;
        1.yield;
    };

    6.do {
        ~r3.next.speak;
        1.yield;
    };

    6.do {
        ~r4.next.speak;
        1.yield;
    };

    6.do {
        ~r5.next.speak;
        1.yield;
    };
};

)

(
r = Routine{
    30.do {
        ~r0.next;
        3.wait;
    };

    "end".postln;
};
r.play;
)

