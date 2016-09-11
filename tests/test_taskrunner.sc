TestSP_Task : GuidoTest {
    test_new {
        var a, b, c;
        var t = SP_Task.new(1, { |time, arg1, arg2| a = time; b = arg1; c = arg2 }, "action", [123, 125]);

        this.expect(t.name).to.be.equal_("action");
        this.expect(t.time).to.be.equal_(1);
        this.expect(t.args).to.be.equal_([123, 125]);
        this.expect(t.asString).to.be.equal_("00:00:01 action 123 125");
        t.run;
        this.expect(a).to.be.equal_(1);
        this.expect(b).to.be.equal_(123);
        this.expect(c).to.be.equal_(125);

        {
            var t2 = SP_Task.newFromString(t.asString);
            this.expect(t2.name).to.be.equal_("action");
            this.expect(t2.time).to.be.equal_(1);
            this.expect(t2.args).to.be.equal_(["123", "125"]);

        }.value;
    }

    test_new_from_string {
        var t = SP_Task.newFromString("invalid");
        this.expect(t).to.be.nil_;

        t = SP_Task.newFromString("1:07 page 1");
        this.expect(t.name).to.be.equal_("page");
        this.expect(t.args).to.be.equal_(["1"]);
        this.expect(t.time).to.be.equal_(67);
    }

    test_as_string {
        var t = SP_Task.new(10);
        this.expect(t.asString).to.be.equal_("00:00:10 default");
    }
}

// SP_Task.test

TestSP_TaskRunner : GuidoTest {
    test_new {
        var tr = SP_TaskRunner.new;
        this.expect(tr.currentTime).to.be.equal_(0);

        tr.currentTime = "0:1:20";
        this.expect(tr.currentTime).to.be.equal_(80);

        tr.currentTime = 68;
        this.expect(tr.currentTime).to.be.equal_(68);
    }

    test_task {
        var t = SP_TaskRunner.new;
        t.addTask("1:25", {}, \task1, []);
        this.expect(t.hasTaskAt(85)).to.be.true_;
        t.removeTask(85);
        this.expect(t.hasTaskAt(85)).to.be.false_;

        t.addTask("10:25", {}, \task1, []);
        this.expect(t.hasTaskAt(625)).to.be.true_;
        t.removeAllTasks;
        this.expect(t.hasTaskAt(625)).to.be.false_;
    }

    test_as_list {
        var t = SP_TaskRunner.new;
        t.addTask("1:25", {}, \task1, []);
        t.addTask("1:25", {}, \task2, [1,2,3]);
        t.addTask("0:25", {}, \task3, [1]);
        this.expect(t.asList).to.be.equal_([
            "00:00:25 task3 1",
            "00:01:25 task1",
            "00:01:25 task2 1 2 3"]);
    }

    test_save {
        var tmp = PathName.tmp +/+ "test.tmp";
        var t = SP_TaskRunner.new;
        t.addTask("1:25", {}, \task1, [1]);
        t.addTask("1:25", {}, \task2, [2,3]);
        t.addTask("0:25", {}, \task3, [3]);

        t.save(tmp);

        {
            var u = SP_TaskRunner.new;
            this.expect(u.load("unknown")).to.be.nil_;
            u.load(tmp);
            this.expect(u.hasTaskAt("1:25")).to.be.true_;
            this.expect(u.hasTaskAt("0:25")).to.be.true_;

            u.removeAllTasks;
            this.expect(u.hasTaskAt("1:25")).to.be.false_;
        }.value;


        {
            var a, b, c;
            var fn = { |n|
                switch(n,
                    \task1, { { |t| a = t } },
                    \task2, { { |t| b = t } },
                    \task3, { { |t| c = t } },
                    { {} }
                )
            };

            var u = SP_TaskRunner.new;
            this.expect(fn.value(\task1)).to.be.a_(Function);
            fn.value(\task1).value(35);
            this.expect(a).to.be.equal_(35);


            u.load(tmp, fn);
            this.expect(u.hasTaskAt("1:25")).to.be.true_;
            this.expect(u.tasksAt("1:25").first.action).to.be.not.nil_;
            u.currentTime = 25;
            u.runTasks;
            this.expect(c).to.be.equal_("3");

            u.currentTime = 85;
            u.runTasks;
            this.expect(a).to.be.equal_("1");
            this.expect(b).to.be.equal_("2");
        }.value;
    }

     test_save2 {
        var tmp = PathName.tmp +/+ "test2.tmp";
        var t = SP_TaskRunner.new;
        t.addTask("1:25", {}, \task1, []);
        t.addTask("1:25", {}, \task2, [1,2,3]);
        t.addTask("0:25", {}, \task3, [1]);
        t.save(tmp);

        this.expect(tmp).exists_;

        {
            var f = File.new(tmp, "r");
            this.expect(f.readAllString).to.be.equal_("00:00:25 task3 1\n00:01:25 task1\n00:01:25 task2 1 2 3\n");
            f.close;
        }.value;

        File.delete(tmp);
    }
}

// SP_TaskRunner.test
