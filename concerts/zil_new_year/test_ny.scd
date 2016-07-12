l = SoundLib.new;
l.listAll;

// load tests
l.load("unknown");
b = l.load("schubert");
x = b.play
x.free;


// play tests
l.load("schubert");
x = l.play("schubert");
x.free

l.load("paper3");
l.loadAll;


x = l.play("rice1");

// all sounds
l.play("schubert");
l.play("onion1");
l.play("onion2");
l.play("rice1");
l.play("paper1");
l.play("paper2");
l.play("paper3");
l.play("metal1");
l.play("metal2");
l.play("microwave2");
l.play("microwave1");

n = NetAddr("127.0.0.1", 7001);
n.sendMsg("/p1/hand_r:tx");
n.sendMsg("/p1/hand_l:tx");

s.boot;
s.quit;