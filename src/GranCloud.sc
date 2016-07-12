GranCloud {

	classvar <>presets;

	var  <>def,		// The grain SynthDef name, or a function returning the name
		<>args,		// Array of name/value pairs of argument values to be passed to the synthdef
		<center,		// An IdentityDictionary of grain attributes
					//   may contain a number, stream, or envelope
					//   an attribute called \rate
		<>dev, 		// An IdentityDictionary of attribute deviations
		<dist,		// An IdentityDictionary defining random functions for dev calculations
					//   Either a symbol representing a predefined random function, or a 
					//   function yielding a multiplier between -1 and 1 that is multiplied
					//   by the dev value with each grain (default: \rand2)
					//   can be:
					// 	  \rand      corresponds to dev.rand    (0 to dev)
					//	  \rand2     corresponds to dev.rand2   (-dev to dev)
					//	  \linrand   corresponds to dev.linrand (linear probability 0 to dev)
					//	  \bilinrand corresponds to dev.bilinrand (linear prob centered at zero)
					//	  \sum3rand  corresponds to dev.sum3rand (quasi gaussian prob curve)					//	  { a Function }  .value is called and the result is multiplied to dev
		<>map,		// An Array of name/value pairs of bus mappings to SynthDef argumets
		<>duration, 	// Number of seconds the cloud should play before stopping
		<order,		// An array of keys from the center/dev dictionaries indicating
					//   a specific processing order.  Required when one center/dev 
					//   calculation depends on the result of another. (default alphabetical)
		<>spec,		// An IdentityDictionary of ControlSpecs for the final center/grain calc
		<>centerSpec, // An IdentityDictionary of ControlSpecs for center values
		<>devSpec,	// An IdentityDictionary of ControlSpecs for dev values
		<>distSpec,	// An IdentityDictionary of ControlSpecs for output of dist
		<loop,		// An IdentityDictionary of loop arrays for attributes
					//   [ loopStart, loopEnd, numLoops ]
		<loopAll,		// A loop array for the cloud as a whole (nil does not loop)
					//   [ loopStart, loopEnd, numLoops ]
		
		<>numChannels,// Number of channels of audio output (for recording functionality in GUI)
		<>timeStretch,// A time multiplier to stetch out or compress the grain duration while
					//   maintaining the shape of the internal attributes
					
		// The following are internal values used for playback and for the interface with 
		// GranCloudGUI objects.		
		<isPlaying,	// Flag indicating the cloud is playing
		<isPaused,	// Flag indicating playback is paused
		<isStopped,	// Flag indicating the stop function is complete
		<>playTime,	// The time the grain has been playing (updated only once per grain)
		<>startTime,  // System time when play starts (SystemClock.seconds)
		<>isScrub,	// A boolean indicating whether the playTime should be changed internally
					//  or externally.  True allows an external control to scrub the playTime.
		<>playLoops, // a boolean indicating whether to play loops or not (default true)		
		<relTime,		// The relative playTime adjusted for loops
		<grainArgs,	// An IdentityDictionary of the last center/dev calculations
		<centerArgs,	// The center portion of the last center/dev calculations
		<devArgs,		// The dev portion of the last center/dev calculations before rand applied
		<randArgs,    // The actual deviation of the last center/dev calc
		
		// These store internal static data calculated before the grain loop to save processing
		<group,
		<loopTime,
		<loopTotal,
		<loopAllTime,
		<loopAllTotal,
		<playCache,
		<envOffset,
		<devEnvOffset;
	
	*initClass {
		presets = Dictionary[
			// simple enveloped sine wave
			"sine_grain" -> IdentityDictionary[
				\def -> { arg out=0, dur, freq, amp, pan;
					Out.ar(out, 
						Pan2.ar(
							SinOsc.ar(freq, 0, amp) * EnvGen.ar(Env.sine(1), timeScale: dur, doneAction: 2),
							pan
						)
					)
				},
				\center -> IdentityDictionary[
					\rate -> 0.01,
					\dur  -> 0.02,
					\freq -> 440,
					\amp  -> 0.2,
					\pan  -> 0
				],
				\dev -> IdentityDictionary[
					\rate -> 0.005,
					\dur  -> 0.01,
					\freq -> 30,
					\amp  -> 0.1,
					\pan  -> 0.5
				],
				\dist -> IdentityDictionary[
					\freq -> \sum3rand,
					\pan  -> \sum3rand,
				],
				\numChannels -> 2
			],
			// Glisson - Sine Wave with sweeping frequency
			"glisson" -> IdentityDictionary[
				\def -> { arg out=0, dur, startFreq, endFreq, amp, pan;
					Out.ar(out, 
						Pan2.ar(
							SinOsc.ar(Line.kr(startFreq, endFreq, dur), 0, amp) * EnvGen.ar(Env.sine(1), timeScale: dur, doneAction: 2),
							pan
						)
					)
				},
				\center -> IdentityDictionary[
					\rate -> 0.01,
					\dur  -> 0.02,
					\startFreq -> 440,
					\endFreq -> 440,
					\amp  -> 0.2,
					\pan  -> 0
				],
				\dev -> IdentityDictionary[
					\rate -> 0.005,
					\dur  -> 0.01,
					\startFreq -> 30,
					\endFreq -> 200,
					\amp  -> 0.1,
					\pan  -> 0.5
				],
				\dist -> IdentityDictionary[
					\pan  -> \sum3rand,
				],
				\numChannels -> 2
			],
			// simple buffer granulator 
			// remember to set a preallocated bufnum in the preset method call, in the \args array, or as a center value
			"buf_grain" -> IdentityDictionary[
				\def -> { arg out=0, bufnum, dur, bufRate, bufStartPos, amp, pan;
					Out.ar(out, 
						Pan2.ar(
							PlayBuf.ar(1, bufnum, bufRate * BufRateScale.kr(bufnum), 1, bufStartPos * BufFrames.kr(bufnum)) * EnvGen.ar(Env.sine(1), timeScale: dur, doneAction: 2),
							pan
						)
					)
				},
				\center -> IdentityDictionary[
					\rate -> 0.05,
					\dur  -> 0.1,
					\bufRate -> 1,
					\bufStartPos -> 0.5,
					\amp  -> 0.2,
					\pan  -> 0
				],
				\dev -> IdentityDictionary[
					\rate -> 0.005,
					\dur  -> 0.01,
					\bufRate -> 0.01,
					\bufStartPos -> 0.01,
					\amp  -> 0.1,
					\pan  -> 0.2
				],
				\dist -> IdentityDictionary[
					\pan  -> \sum3rand,
				],
				\numChannels -> 2
			]
		];	
	}
	
	// Create a new GranCloud object using a user defined SynthDef
	*new { arg def, args, center, dev, dist, map, duration, order, spec, centerSpec, devSpec, distSpec, loop, loopAll, numChannels=2, timeStretch=1;
		^super.newCopyArgs(def, args, center, dev, dist, map, duration, order, spec, centerSpec, devSpec, distSpec, loop, loopAll, numChannels, timeStretch).init;
	}
	
	// Create a new GranCloud object using a class preset SynthDef
	// (Parameters after preset are optional and used only by certain presets)
	*preset { arg server, preset="sine_grain", bufnum;
		var obj, defName;
		preset = preset.asString;
		obj = GranCloud.new;
		presets[preset].keysValuesDo({ arg key, value; 
			obj.instVarPut(key, value.deepCopy);
		});
		defName = "GranCloud_" ++ preset;
		SynthDef(defName, obj.def).send(server);
		obj.def = defName;
		if(bufnum.isNumber, { 
			obj.args = obj.args.addAll([ \bufnum, bufnum ]);
		});
		^obj
	}
	
	init {
		center       = center      ? IdentityDictionary[ (\rate -> 0.1) ];
		dev          = dev         ? IdentityDictionary.new;
		loop         = loop        ? IdentityDictionary.new;
		spec         = spec        ? IdentityDictionary.new;
		centerSpec   = centerSpec  ? IdentityDictionary.new;
		devSpec      = devSpec     ? IdentityDictionary.new;
		distSpec     = distSpec    ? IdentityDictionary.new;
		dist         = dist        ? IdentityDictionary.new;
		envOffset    = IdentityDictionary.new;
		devEnvOffset = IdentityDictionary.new;
		args         = args        ? Array.new;
		map          = map         ? Array.new;
		duration     = duration    ? inf;
		order        = order       ? Array.new;
		isPlaying    = false;
		isPaused     = false;
		isStopped    = true;
		isScrub      = false;
		playLoops    = true;
		playTime     = 0;
		this.updateStatus;
	}
	
	center_ { arg dict;
		center = dict;
		this.updateOrder;
	}
	
	// setter methods
	order_ { arg array;
		order = array;
		this.updateOrder;
	}
	
	loop_ { arg dict;
		loop = dict;
		this.updateLoopData;
	}
	
	loopAll_ { arg array;
		loopAll = array;
		this.updateLoopData;
	}
	
	dist_ { arg dict;
		dist = dict;
		this.updateRandomFunc
	}
	
	// This method updates the status of variables that are pre-calculated outside of the grain
	// loop.  It should be called if new grain attributes are added, the processing
	// order, or loop data is changed while the cloud is playing
	updateStatus {
		this.updateOrder;
		this.updateLoopData;
		this.updateRandomFunc;
		timeStretch = timeStretch ? 1;
	}
	
	// resets the play time for the cloud
	reset { arg time = 0;
		startTime = SystemClock.seconds - time;
		this.updateStatus;
	}
	
	// render the cloud
	play { arg server, out=0, target, offset=0, addAction=\addToHead;
			
		// initialize some variables
		isPlaying = true;
		isPaused  = false;
		isStopped = false;
		server    = server ? Server.default;
		
		playCache = [ server, out, target, offset, addAction ];
		envOffset   = IdentityDictionary.new;
		devEnvOffset = IdentityDictionary.new;
		
		
		// create group for the cloud
		group = Group.new(target ? server, addAction);
		
		// get the startTime
		startTime = SystemClock.seconds;
		if(isScrub.not, { playTime = offset });
		
		// make sure statuses are updated before starting
		this.updateStatus;
				
		// start the grain loop
		SystemClock.sched(0, { arg clockTime;
			
			var nodeID, thisDef, arguments;
						
			// calculate the playTime and get the attribute values
			if(isScrub.not, { playTime = (clockTime - startTime + offset) / timeStretch });
			arguments = this.nextGrainArgs(playTime).add(\out).add(out);
			if( args.size > 0, { arguments = arguments ++ args });
			
			// get the SynthDef name
			thisDef = if(def.respondsTo(\next), { def.next(relTime) }, { def.value(relTime) } );

			// send the synth messages
			if( map.size > 0, { 
				nodeID = server.nextNodeID;
				server.sendBundle(server.latency,  
					["/s_new", thisDef, nodeID, 1, group.nodeID ] ++ arguments,
					[ "/n_map", nodeID ] ++ map
				)
			}, {
				server.sendMsg("/s_new", thisDef, -1, 1, group.nodeID, *arguments);
			});
			
			// give scheduler next value or stop
			if((isPlaying && (playTime < duration)), { 
				grainArgs[\rate] 
			}, {
				if(isPlaying && isPaused.not, { this.stop });
				nil 
			})
		})

	}
	
	pause {
		if(isPaused, {
			playCache[3] = playTime;
			this.play(*this.playCache);
		}, {
			isPaused = true;
			isPlaying = false;
		})
	}
	
	stop {
		isPlaying = false;
		playTime  = 0;
		SystemClock.sched((grainArgs[\dur] ? 0.5 ) + ( playCache[0].latency ? 1.0 ), {
			group.free; 
			group = nil;
			startTime = nil;
			isStopped = true;	
		})
	}
	
	at { arg name;
		^center.at(name)
	}
	
	put { arg name, value;
		center.put(name, value)
	}

	// allows dynamic placement of new envelope while playing
	putEnv { arg name, env;
		envOffset[name] = playTime;
		center.put(name, env);
	}
	
	// allows dynamic placement of new envelope while playing 
	putDevEnv { arg name, env;
		devEnvOffset[name] = playTime;
		dev.put(name, env);
	}
	
	// METHODS BELOW HERE ARE INTENDED TO BE PRIVATE AND ARE USED BY THE GranCloudGUI CLASS

	// only needed when centerSpecs and devSpecs are needed, but not populated and a GUI is used
	updateOrder {
	
		if(order.size == 0, {
			order = center.keys.asArray.sort;
		}, {
			// remove anything in order that is not in center
			order = order.select({ arg item; center.keys.includes(item) });
			
			// add everything in center that is not in order
			order = order.addAll(center.keys.asArray.select({ arg item; order.includes(item).not }).asArray.sort);
		});
		^order
					
	}
	
	updateLoopData {
		// do some pre-calculation on loop times to save processing later
		if(loopAll.notNil, {
			loopAllTime  = loopAll[1] - loopAll[0];
			loopAllTotal = loopAllTime * loopAll[2];
		});

		// do the same for attribute loops
		loopTime  = IdentityDictionary.new;
		loopTotal = IdentityDictionary.new;
		loop.keysValuesDo({ arg key, value;
			if(loop[key].notNil, {
				loopTime.put(key,  value[1] - value[0]);
				loopTotal.put(key, loopTime[key] * value[2]);
			})
		})
	}
	
	updateRandomFunc {
		order.do({ arg attr; dist[attr] = dist[attr] ? \rand2 });
	}
	
	defaultSpecs {
		this.updateStatus;
		this.nextGrainArgs;
		order.do({ arg attr;
			spec[attr] = spec[attr] ? if(attr == \rate, { [ 0.001, 1.0, 'lin' ].asSpec }, { attr.asSpec }) ? [ 0, 1, 'lin' ].asSpec;
			centerSpec[attr] = centerSpec[attr] ? spec[attr] ? [ 0, centerArgs[attr] * 3 ].asSpec;
			devSpec[attr] = devSpec[attr] ? [ 0, centerSpec[attr].maxval / 2, centerSpec[attr].warp.asSpecifier ].asSpec;

			// there are problems with the warp object when specs are copied this gets past them
			centerSpec[attr] = centerSpec[attr].storeArgs.asSpec;
			devSpec[attr]    = devSpec[attr].storeArgs.asSpec;
			
			// add some checks for bad values
			if((spec[attr].warp.asSpecifier.asSymbol == 'exp') && (spec[attr].minval.sign != spec[attr].maxval.sign), {
				spec[attr].minval = 0.00001 * spec[attr].maxval.sign
			});
			if((centerSpec[attr].warp.asSpecifier.asSymbol == 'exp') && (centerSpec[attr].minval.sign != spec[attr].maxval.sign), {
				centerSpec[attr].minval = 0.00001 * centerSpec[attr].maxval.sign
			});
			if((devSpec[attr].warp.asSpecifier.asSymbol == 'exp') && (devSpec[attr].minval.sign != devSpec[attr].maxval.sign), {
				devSpec[attr].minval = 0.00001 * devSpec[attr].maxval.sign
			});
			
			if(spec[attr].warp.class.name.asSymbol == \FaderWarp, {
				if(spec[attr].maxval < 1.0, { spec[attr].maxval = 1 })
			});
			if(centerSpec[attr].warp.class.name.asSymbol == \FaderWarp, {
				if(centerSpec[attr].maxval < 1.0, { centerSpec[attr].maxval = 1 })
			});
			if(devSpec[attr].warp.class.name.asSymbol == \FaderWarp, {
				if(devSpec[attr].maxval < 1.0, { devSpec[attr].maxval = 1 })
			});		
		})
	}
	
	// get the relative time within a loopAll loop for use as a time pointer for the cloud
	updateRelTime { arg playTime=0;

		if(loopAll.notNil && playLoops, {
			
			if(playTime < loopAll[0], {
				// before the loop
				relTime = playTime;
			}, {
				if(playTime < (loopAllTotal + loopAll[0]), {
					// in the loop
					relTime = (loopAll[0] + ((playTime - loopAll[0]) % loopAllTime));
				}, {
					// after the loop
					relTime = (playTime + loopAllTime - loopAllTotal);
				})
			})
		}, {
			// no looping
			relTime = playTime
		});
		^relTime
	}
	
	// get the relative time within an attribute loop for a time pointer for that attrubute
	getAttrRelTime { arg attr, playTime=0;
		
		if(loop[attr].notNil && playLoops, {
			
			if(relTime < loop[attr][0], {
				// before the loop
				^relTime;
			}, {
				if(relTime < (loopTotal[attr] + loop[attr][0]), {
					// in the loop
					^(loop[attr][0] + ((relTime - loop[attr][0]) % loopTime[attr]));
				}, {
					// after the loop
					^(relTime + loopTime[attr] - loopTotal[attr]);
				})
			})
		}, {
			// no loops
			^relTime
		})
		
	}

	nextGrainArgs { arg playTime=0, ignoreLoop=false;
	
		var thisRelTime, attrRelTime, className, grainArgList;
		
		grainArgs  = IdentityDictionary.new;
		centerArgs = IdentityDictionary.new;
		devArgs    = IdentityDictionary.new;
		randArgs   = IdentityDictionary.new;
		grainArgList = List.new;
		
		thisRelTime = if(ignoreLoop, {
			playTime
		}, {
			this.updateRelTime(playTime)
		});
		
		order.do({ arg attr;
		
			// get the attr relative time
			attrRelTime = if(ignoreLoop, {
				playTime
			}, {
				this.getAttrRelTime(attr, playTime);  // maybe this should be relTime instead
			});
		
			// get the center value
			centerArgs[attr] = if(center[attr].respondsTo('at'), {
				if(envOffset[attr].notNil, { 
					center[attr].at(attrRelTime - envOffset[attr])
				}, {
					center[attr].at(attrRelTime)
				})   
			}, { 
				if(center[attr].class == Function, {
					center[attr].value(grainArgs, centerArgs, devArgs, randArgs, attrRelTime)
				}, {
					center[attr].next(attrRelTime)
				})
			});
			
			// constrain center value to spec
			if(centerSpec[attr].notNil, {
				centerArgs[attr] = centerSpec[attr].constrain(centerArgs[attr])
			});
			
			
			// get the dev value
			if(dev[attr].isNil, {
				devArgs[attr] = 0;
			}, {
				devArgs[attr] = if(dev[attr].respondsTo('at'), {
					if(devEnvOffset[attr].notNil, {
					    dev[attr].at(attrRelTime - devEnvOffset[attr])
					}, {
						dev[attr].at(attrRelTime)
					})   
				}, { 
					if(dev[attr].class == Function, {
						dev[attr].value(grainArgs, centerArgs, devArgs, randArgs, attrRelTime)
					}, {
						dev[attr].next(attrRelTime)
					})
				})
			});
			
			// constrain the dev value
			if(devSpec[attr].notNil, {
				devArgs[attr] = devSpec[attr].constrain(devArgs[attr])
			});			
			
			// determine the actual value based on the random deviation 
			randArgs[attr] = if(dist[attr].class.name == \Function, {
				dist[attr].value(devArgs[attr], grainArgs, centerArgs, devArgs, randArgs, attrRelTime)
			}, {
				devArgs[attr].perform(dist[attr])
			});
			
			// constrain the rand value
			if(distSpec[attr].notNil, {
				randArgs[attr] = distSpec[attr].constrain(randArgs[attr])
			});
			
			// get the final value constrained to a spec
			grainArgs[attr] = centerArgs[attr] + randArgs[attr];
			
			// constrain final value to spec
			if(spec[attr].notNil, {
				grainArgs[attr] = spec[attr].constrain(grainArgs[attr])
			})

		});
		
		// build the list format too
		grainArgs.keysValuesDo({ arg key, value; grainArgList.add(key).add(value) });
		
		^grainArgList
	}
	
}