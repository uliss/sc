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




p = Patch(Instr("utils.click") <>> Instr("route.->phones|"), (phones_ch: 4, bpm: 240, amp: 8))
p.gui
p.play
p.stop


o = Server.local.options;
o.numOutputBusChannels.postln;


