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
n.sendMsg("/dvoinik", \start);
n.sendMsg("/dvoinik", \stop);
n.sendMsg("/dvoinik", \pos1, -1);
n.sendMsg("/dvoinik", \pos1, 1);
n.sendMsg("/dvoinik", \pos2, 1);
n.sendMsg("/dvoinik", \pos2, -1);


///
// DRAZHE MANUAL CONTROL
n.sendMsg("/drazhe", \start);
n.sendMsg("/drazhe", \stop);
n.sendMsg("/drazhe", \freeze, 1);
n.sendMsg("/drazhe", \freeze, 0);


/// KURANTY
n.sendMsg("/kuranty", \start);
n.sendMsg("/kuranty", \stop);
n.sendMsg("/kuranty", \release, 2);

/// WIND
n.sendMsg("/wind", \start);
n.sendMsg("/wind", \stop);
n.sendMsg("/wind", \release, 1);
~scenes.set_wind(\amp, 1);


/////////////////////////////////
/// FADER  MANUAL CONTROL
/////////////////////////////////
n.sendMsg("/xfader", \start);
n.sendMsg("/xfader", \stop);
n.sendMsg("/xfader", \release, 2);
n.sendMsg("/xfader", \xfade, -1);
n.sendMsg("/xfader", \xfade, -0.9);
n.sendMsg("/xfader", \xfade, 0);
n.sendMsg("/xfader", \xfade, 0.7);
~scenes.set_xfader(\amp, 1);