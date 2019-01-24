package com.lua.platform;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

public class LuaPlatform {
	private static Log logger = LogFactory.getLog(LuaLibrary.class);
	private Globals globals = JsePlatform.standardGlobals();

	public LuaPlatform() {
		globals.load(new LuaLibrary());
		LoadState.install(globals);
		org.luaj.vm2.compiler.LuaC.install(globals);

		load();

		String t = globals.get("package").get("path").tojstring();
		logger.info("package.path:" + t);
	}

	private void load() {

		// json
		LuaValue chunk = globals.loadfile("scripts/utils/JSON.lua").call();
		globals.get("package").get("loaded").set("utils.JSON", chunk);

		// jtb
		chunk = globals.loadfile("scripts/protocol/jtb.lua").call();
		globals.get("package").get("loaded").set("protocol.jtb", chunk);

		// zjb
		chunk = globals.loadfile("scripts/protocol/zjb.lua").call();
		globals.get("package").get("loaded").set("protocol.zjb", chunk);

		// corex
		chunk = globals.loadfile("scripts/luaex/corex.lua").call();
		globals.get("package").get("loaded").set("luaex.corex", chunk);

		// def
		chunk = globals.loadfile("scripts/pos/def.lua").call();
		globals.get("package").get("loaded").set("pos.def", chunk);

		// calmac
		chunk = globals.loadfile("scripts/pos/oper/calmac.lua").call();
		globals.get("package").get("loaded").set("pos.oper.CalMac", chunk);

		// export
		chunk = globals.loadfile("scripts/pos/oper/export.lua").call();
		globals.get("package").get("loaded").set("pos.oper.Export", chunk);

		// crypt
		chunk = globals.loadfile("scripts/pos/oper/crypt.lua").call();
		globals.get("package").get("loaded").set("pos.oper.Crypt", chunk);

		// route
		chunk = globals.loadfile("scripts/pos/route.lua").call();
		globals.get("package").get("loaded").set("pos.route", chunk);

	}

	public String execute(String request) {

		LuaValue chunk = globals.loadfile("scripts/script.lua").call();
		LuaValue func = chunk.get(LuaValue.valueOf("dispatch"));
		return func.call(LuaValue.valueOf(request)).toString(); 

	}
}
