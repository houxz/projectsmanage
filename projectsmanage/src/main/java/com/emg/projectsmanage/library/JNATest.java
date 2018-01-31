package com.emg.projectsmanage.library;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.WString;

public interface JNATest extends Library {
	Properties props = System.getProperties();
	String bits = String.valueOf(props.get("sun.arch.data.model"));
	Path path = Paths.get("dll", (Platform.isWindows() ? "windows" : "linux") + (bits.equals("32") ? "32" : "64"), "WebDll");

	JNATest Instance = (JNATest) Native.loadLibrary(path.toString(), JNATest.class);

	public void say(WString value);

}
