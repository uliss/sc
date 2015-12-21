(
~scenes = Scenes.new(~l);
r = Routine {
    inf.do {
        if(~scenes.person1.handsUp) {

        };
        // ~scenes.person1.noHands.postln;
        1.wait;
    }
};

r.play;
// n.sendMsg("/wind", \start);
// n.sendMsg("/wind", \stop);
// n.sendMsg("/wind", \release, 1);
)