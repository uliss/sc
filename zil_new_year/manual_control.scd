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
~scenes.init;
n.sendMsg("/dvoinik", \start);
n.sendMsg("/dvoinik", \stop);
n.sendMsg("/dvoinik", \pos1, -1);
n.sendMsg("/dvoinik", \pos1, 1);
n.sendMsg("/dvoinik", \pos2, 1);
n.sendMsg("/dvoinik", \pos2, -1);
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
n.sendMsg("/seledka", \start);
n.sendMsg("/seledka", \part1);
n.sendMsg("/seledka", \part2);
n.sendMsg("/seledka", \part3);
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
