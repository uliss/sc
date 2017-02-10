SoundCard.mbox

NodeJS.start
NodeJS.reload
NodeJS.isRunning

~spiral = Piece_Davidson_Spiral.new
~spiral.add
~spiral.play
~spiral.stop


~king = Piece_King_Revelations.new
~king.add
~king.play
~king.stop

~cardew = Piece_BasicControl.new(nButtons: 8, nToggles: 5, nFaders: 5)
~cardew.add
~cardew.free


(
~label = GuidoLabelApp.new;
~cardew.bindButton(0, { NodeJS.css("html", "background-color", "grey");    ~label.text = "..."; });
~cardew.bindButton(1, { NodeJS.css("html", "background-color", "#5f7821"); ~label.text = "A"; });
~cardew.bindButton(2, { NodeJS.css("html", "background-color", "#5c5f9d"); ~label.text = "B"; });
~cardew.bindButton(3, { NodeJS.css("html", "background-color", "#37627b"); ~label.text = "C"; });
~cardew.bindButton(4, { NodeJS.css("html", "background-color", "#eccb7a"); ~label.text = "D"; });
~cardew.bindButton(5, { NodeJS.css("html", "background-color", "#a2807e"); ~label.text = "E"; });
~cardew.bindButton(6, { NodeJS.css("html", "background-color", "#df8dc7"); ~label.text = "F"; });
~cardew.bindButton(7, { NodeJS.css("html", "background-color", "#ffaa2f"); ~label.text = "G"; });
)

~label.text = "A"


a = List.new
a.add(2)
a
1 < 2 && 3 > 4


p = Patch(Instr("utils.click") <>> Instr("route.->phones|"), (phones_ch: 4, bpm: 240, amp: 8))
p.gui
p.play
p.stop


o = Server.local.options;
o.numOutputBusChannels.postln;


