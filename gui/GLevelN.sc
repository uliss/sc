GLevelN : CompositeView {
    var levels;

    *new {
        arg parent, bounds = Rect(0, 0, 102, 160), number = 5;
        ^super.new(parent, bounds).init(number);
    }

    init {
        arg num;

        levels = Array.new(32);

        num.do( {
            arg i;
            levels.add(GLevel.new(this, title: "ch" ++ i));
            levels[i].moveTo(i * 25, 0);
            levels[i].level.value = 0;
            levels[i].level.peakLevel_(0);
        });

        super.resizeTo(num * 25, 160);
    }

    title {
        arg idx, title;
        levels[idx].title(title);
    }

    item {
        arg idx;
        ^levels[idx];
    }

    value {
        arg idx, amp;
        levels[idx].level.value = amp.ampdb.linlin(-60, 0, 0, 1);
    }

    peakLevel {
        arg idx, amp;
        levels[idx].level.peakLevel_(amp.ampdb.linlin(-60, 0, 0, 1));
    }
}