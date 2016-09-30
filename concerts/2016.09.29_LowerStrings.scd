s.boot;
s.quit
s.reboot
NodeJS.restart
s.options.device = "Mbox Pro"
NodeJS.restart(debugLevel: "debug")

(
s.makeWindow.window.alwaysOnTop_(true).background_(\red).name_(s.options.device);
)


/// PALABRAS
(
a = Piece_Bergner_Palabras.new;
a.add;
)
a.open;
a.play;
a.stop;
// END
a.free;

// BACHIANA
(
b = Piece_Shannon_Bachiana.new;
b.add;
)
b.play;
b.stop;
// END
b.free;

// FILONENKO
(
c = Piece_Filonenko_JacksonG.new;
c.add;
)

c.play;
c.stop;
c.free;


s.quit
