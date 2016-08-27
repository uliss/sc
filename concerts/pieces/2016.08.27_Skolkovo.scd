(
s.quit;
ServerBoot.add({NodeJS.restart}, \default);
{s.boot}.defer(2);
)

NodeJS.redirect("/piece")
NodeJS.reload
NodeJS.verbose(true)
NodeJS.verbose(false)

~part = Piece_Part_Spiegel_im_Spiegel.new
~part.add
~part.loadParams

~myssi = Piece_Mazzoli_Tooth_and_Nail.new
~myssi.play
~myssi.loadParams
~myssi.stop

~ground = Piece_Davidson_Message_Ground.new.toPage(2)
~ground.play
~ground.add
~ground.stop

~spiral = Piece_Davidson_Spiral.new.toPage(2)
~spiral.add
~spiral.play
~spiral.stop

-20.dbamp

~vsolo = Piece_Viola_Solo.new
~vsolo.add

Instr("oct", {
    arg in;
    PitchShift.ar(in, 0.1, 0.5);
});


(
Instr("boost", {
    arg in;
    in * 4;
}, [AudioSpec(1)], AudioSpec(1));
i = Instr("viola.in") <>> Instr("viola.compress") <>> Instr("boost") <>> Instr("viola.reverb") <>> Instr("common.pan2");
p = Patch(i);
p.gui
)






