// OSC control addr
// n = NetAddr("127.0.0.1", 7000);
n = NetAddr("alex", 10000);
n.sendMsg("/freeze", 1);
n.sendMsg("/freeze", 0);

k.gadanie.play_synth1;
k.gadanie.stop_synth1;
k.gadanie.play_synth2;
k.gadanie.stop_synth2;
k.gadanie.debug = true;
k.gadanie.debug = false;
k.dumpKinect(true);
k.dumpKinect(false);

{SinOsc.ar([440, 460], mul: 0.01)}.play


/////////////////
///    START
/////////////////
// k = NYSketches.new(NetAddr("localhost", 10001));
k = NYSketches.new(NetAddr("alex", 10000));

//  гаснет свет
k.gadanie.start; // сделать длинее хвост - в начале тише звуки (!!!)
k.gadanie.stop; // может быть позже?

k.gadanie.calibrate;
k.gadanie.person1.dump(\head);
k.gadanie.reload; // NB!

// появляется синяя заливка
k.bubbles.start
k.gadanie.stop;
k.bubbles.change_bass
k.bubbles.release
// k.bubbles.stop

k.stol.start_in
k.stol.stop
// k.stol.start_out

k.seledka.reload; //NB!
k.seledka.play1;
// k.seledka.beat_onion2
// выход девушек 4 раза - ОКОЛО СТОЛА
k.seledka.play2;
// выход девушек назад - в линию
k.seledka.play3;
// УНОСИМ СТОЛ СРАЗУ!!!!

// уносим стол
k.seledka.stop;

// (!) Я включаю
k.dvoinik.start;
// Я ВКЛЮЧАЮ ФОНАРЬ в 1:15
// выключить фонарь когда вынесли елку
k.dvoinik.pause;
k.dvoinik.resume;
k.dvoinik.stop;
n = NetAddr("alex", 10000);
n.sendMsg("/freeze", 0);


k.drazhe.thresholdStart = 0.15;
k.drazhe.thresholdStop = 0.13;
k.drazhe.start;
k.drazhe.freeze(true)
k.drazhe.freeze(false)
// только после проигрыша в конце
k.drazhe.release(5);
k.drazhe.stop;

// девушки катаются по полу
k.metel.start

// начинают прыгать далеко за кинектом
k.mixer.play3; // interactive mixer

// девушки уходят из зоны кинекта, остается вьюга, я выключаю
k.mixer.stop;

// появляется елка - шар
// k.final.set(\amp, 0.5);
k.final.start
k.metel.volume(0.1, 5)
k.final.release(20)
k.metel.volume(0.05, 5)
// выносить елку
k.metel.release(15)
k.final.stop

