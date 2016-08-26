(
SoundCard.saffire;
ServerBoot.add({NodeJS.restart}, \default);
{s.boot}.defer(2);
)

NodeJS.redirect("/piece")
NodeJS.reload

~part = Piece_Part_Spiegel_im_Spiegel.new
~part.add
~part.loadParams

~myssi = Piece_Mazzoli_Tooth_and_Nail.new
~myssi.play
~myssi.loadParams
~myssi.stop

~ground = Piece_Davidson_Message_Ground.new.toPage(2)
~ground.play
~ground.stop
