/////////////////////////////////
/// GADANIE  MANUAL CONTROL
/////////////////////////////////

// PERSON 1
n.sendMsg("/gadanie/0", 1);
n.sendMsg("/gadanie/0", 0);
// PERSON 2
n.sendMsg("/gadanie/1", 1);
n.sendMsg("/gadanie/1", 0);



/////////////////////////////////
/// DVOINIK  MANUAL CONTROL
/////////////////////////////////
d = DvoinikScene.new([\delay, 2]);
d.start;
d.pause;
d.resume;
d.stop;
d.release(4);
d.amp(2);
// osc
n.sendMsg("/dvoinik", \start);
n.sendMsg("/dvoinik", \pause);
n.sendMsg("/dvoinik", \resume);
n.sendMsg("/dvoinik", \stop);
n.sendMsg("/dvoinik", \release, 2);
n.sendMsg("/dvoinik", \amp, 2);


///
// DRAZHE MANUAL CONTROL
n.sendMsg("/drazhe", \start);
n.sendMsg("/drazhe", \stop);
n.sendMsg("/drazhe", \freeze, 1);
n.sendMsg("/drazhe", \freeze, 0);

~scenes.drazheParam[\timeout] = 0.15;
~scenes.drazheParam[\acc_threshold_up] = 0.21; // 0.11
~scenes.drazheParam[\acc_threshold_down] = 0.1; // 0.7


/// KURANTY
n.sendMsg("/kuranty", \start);
n.sendMsg("/kuranty", \stop);
n.sendMsg("/kuranty", \release, 2);

/// WIND
~scenes.init;
n.sendMsg("/wind", \start);
n.sendMsg("/wind", \stop);
n.sendMsg("/wind", \release, 1);
n.sendMsg("/wind", \amp, 0.4);
n.sendMsg("/wind", "set", "amp", 1.01);
n.sendMsg("/wind", "set", "rate1", 2);
n.sendMsg("/wind", "set", "rate2", 1.1);


/// SELEDKA
t = SeledkaScene.new(kinectPerson1: ~scenes.person1, kinectPerson2: ~scenes.person2);
t.play1;
t.play2;
t.beat_metal1;
t.beat_metal2;
t.beat_onion1;
t.beat_onion2;
t.beat_microwave;
t.beat_bass;
t.play3;
t.stop;


n.sendMsg("/seledka", \start);
n.sendMsg("/seledka", \start, 1);
n.sendMsg("/seledka", \start, 2);
n.sendMsg("/seledka", \start, 3);
n.sendMsg("/seledka", \stop);

/////////////////////////////////
/// FADER  MANUAL CONTROL
/////////////////////////////////
~scenes.init;
n.sendMsg("/xfader", \start);
n.sendMsg("/xfader", \stop);
n.sendMsg("/xfader", \release, 2);
n.sendMsg("/xfader", \xfade, -1);
n.sendMsg("/xfader", \xfade, -0.9);
n.sendMsg("/xfader", \xfade, 0);
n.sendMsg("/xfader", \xfade, 0.7);
n.sendMsg("/xfader", \amp, 0.1);
n.sendMsg("/xfader", \amp, 1);
