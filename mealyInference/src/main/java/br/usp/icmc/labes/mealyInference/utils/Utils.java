package br.usp.icmc.labes.mealyInference.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sat4j.specs.TimeoutException;

import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.base.IFeatureModelFactory;
import de.ovgu.featureide.fm.core.base.impl.FMFactoryManager;
import de.ovgu.featureide.fm.core.configuration.Configuration;
import de.ovgu.featureide.fm.core.configuration.Selection;
import de.ovgu.featureide.fm.core.io.manager.SimpleFileHandler;
import de.ovgu.featureide.fm.core.io.xml.XmlFeatureModelFormat;
import de.ovgu.featureide.fm.core.job.monitor.NullMonitor;
import net.automatalib.automata.transout.impl.compact.CompactMealy;
import net.automatalib.automata.transout.impl.compact.CompactMealyTransition;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;
import net.automatalib.words.WordBuilder;
import net.automatalib.words.impl.Alphabets;

public class Utils {


	public static final Word<String> OMEGA_SYMBOL = Word.fromLetter("Ω");

	private static final String WORD_DELIMITER = ";";
	private static final String SYMBOL_DELIMITER = ",";
	
	private static Utils instance;
	
	private Utils() { }
	
	public static Utils getInstance() {
		if(instance == null){
			Utils.instance = new Utils();
		}
		return instance;
	}
	
	public Map<String,List<String>> loadConfigurations(File configurationsFile) throws FileNotFoundException,IOException, TimeoutException{
		Map<String,List<String>> splsWithProds = new LinkedHashMap<String,List<String>>();

		BufferedReader spls = new BufferedReader(new FileReader(configurationsFile));

		String line;
		String splname;
		
		while (spls.ready()) {
			line = spls.readLine();
			if(line.length()>1){
				line = line.replaceFirst("./", "/");
			}
			splname = line.replaceFirst("/fsm/configurations_fsm", "example");
			splname = splname.replaceFirst("txt$", "xml");
			
			BufferedReader br = new BufferedReader(new FileReader(new File(configurationsFile.getParentFile(),line)));
			
			line = br.readLine();

			IFeatureModelFactory factory = FMFactoryManager.getDefaultFactory();
			IFeatureModel model = factory.createFeatureModel();


			List<String> prods = new ArrayList<>();
			
			while (br.ready()) {
				line = br.readLine();
				String[] conf_info = line.split("\t");
				
				String prodFsm = conf_info[0]+".txt";
				
				conf_info[0] = splname;

				
				// load FM using FeatureIDE
				File fm_file = new File(configurationsFile.getParentFile(),"/feature_models/"+conf_info[0]);
				SimpleFileHandler.load(fm_file.toPath(), model,new XmlFeatureModelFormat());


				// evaluate configuration
				Configuration conf = new Configuration(model);
				
				for (String featOn : conf_info[1].split(" ")) {
					conf.setManual(featOn, Selection.SELECTED);
				}
				if(conf.isValid()){
					prods.add(prodFsm);
				}
			}

			splsWithProds.putIfAbsent(splname, prods);
		}
		
		
		return splsWithProds;
	}


	public CompactMealy<String, Word<String>> loadMealyMachine(File f) throws Exception {

		Pattern kissLine = Pattern.compile("\\s*(\\S+)\\s+--\\s+(\\S+)\\s*/\\s*(\\S+)\\s+->\\s+(\\S+)\\s*");

		BufferedReader br = new BufferedReader(new FileReader(f));

		List<String[]> trs = new ArrayList<String[]>();

		List<String> abc = new ArrayList<>();

		//		int count = 0;

		while(br.ready()){
			String line = br.readLine();
			Matcher m = kissLine.matcher(line);
			if(m.matches()){
				//				System.out.println(m.group(0));
				//				System.out.println(m.group(1));
				//				System.out.println(m.group(2));
				//				System.out.println(m.group(3));
				//				System.out.println(m.group(4));

				String[] tr = new String[4];
				tr[0] = m.group(1);
				tr[1] = m.group(2); 
				if(!abc.contains(tr[1])){
					abc.add(tr[1]);
				}
				tr[2] = m.group(3);
				tr[3] = m.group(4);
				trs.add(tr);
			}
			//			count++;
		}

		br.close();

		Alphabet<String> alphabet = Alphabets.fromCollection(abc);
		CompactMealy<String, Word<String>> mealym = new CompactMealy<String, Word<String>>(alphabet);

		Map<String,Integer> states = new HashMap<String,Integer>();
		Integer si=null,sf=null;

		Map<String,Word<String>> words = new HashMap<String,Word<String>>();		


		WordBuilder<String> aux = new WordBuilder<>();

		aux.clear();
		aux.append(OMEGA_SYMBOL);
		words.put(OMEGA_SYMBOL.toString(), aux.toWord());


		for (String[] tr : trs) {
			if(!states.containsKey(tr[0])) states.put(tr[0], mealym.addState());
			if(!states.containsKey(tr[3])) states.put(tr[3], mealym.addState());

			si = states.get(tr[0]);
			sf = states.get(tr[3]);

			if(!words.containsKey(tr[1])){
				aux.clear();
				aux.add(tr[1]);
				words.put(tr[1], aux.toWord());
			}
			if(!words.containsKey(tr[2])){
				aux.clear();
				aux.add(tr[2]);
				words.put(tr[2], aux.toWord());
			}
			mealym.addTransition(si, words.get(tr[1]).toString(), sf, words.get(tr[2]));
		}

		for (Integer st : mealym.getStates()) {
			for (String in : alphabet) {
				//				System.out.println(mealym.getTransition(st, in));
				if(mealym.getTransition(st, in)==null){
					mealym.addTransition(st, in, st, OMEGA_SYMBOL);
				}
			}
		}


		mealym.setInitialState(states.get(trs.get(0)[0]));

		return mealym;
	}


	public CompactMealy<String, Word<String>> loadMealyTrace(File io) throws Exception {
		

		BufferedReader br = new BufferedReader(new FileReader(io));

		Set<String[]> ioSet = new LinkedHashSet<String[]>();
		
		Set<String> abc = new HashSet<>();

//		while(br.ready())
		{
			String line = br.readLine();
			if(!line.isEmpty()){
				String[] words = line.split(SYMBOL_DELIMITER);
				for (int i = 0; i < words.length; i+=2) {
					abc.add(words[i]);
				}
				ioSet.add(words);
			}
		}
		br.close();
		
		Alphabet<String> alphabet = Alphabets.fromCollection(abc);
		CompactMealy<String, Word<String>> omegaMachine = new CompactMealy<String, Word<String>>(alphabet);
		
		Integer si = omegaMachine.addState();
		omegaMachine.setInitialState(si);
		
		for (String[] ioItem : ioSet) {
			si = omegaMachine.getInitialState();
			for (int i = 0; i < ioItem.length; i+=2) {
				CompactMealyTransition<Word<String>> tr = omegaMachine.getTransition(si, ioItem[i]);
				if(tr == null) {
					Word<String> out = Word.epsilon();
					out = out.append(ioItem[i+1]);
					tr = omegaMachine.addTransition(si, ioItem[i], omegaMachine.addState(), out);
				}
				si = tr.getSuccId();
			}
		}		

		
		return omegaMachine;
	}

	public CompactMealy<String, Word<String>> loadMealyMachineFromDot(File f) throws Exception {

		Pattern kissLine = Pattern.compile("\\s*(s[0-9]+)\\s+->\\s+(s[0-9]+)\\s*\\[label=[\"<](.+)[\">]\\];?");

		BufferedReader br = new BufferedReader(new FileReader(f));

		List<String[]> trs = new ArrayList<String[]>();

		Set<String> abc = new HashSet<>();

		//		int count = 0;

		while(br.ready()){
			String line = br.readLine();
			Matcher m = kissLine.matcher(line);
			if(m.matches()){
				//				System.out.println(m.group(0));
				//				System.out.println(m.group(1));
				//				System.out.println(m.group(2));
				//				System.out.println(m.group(3));
				//				System.out.println(m.group(4));

				String[] tr = new String[4];
				tr[0] = m.group(1);
				tr[1] = m.group(3); 
//				if(!abc.contains(tr[1])){
//					abc.add(tr[1]);
//				}
//				tr[2] = m.group(4);
				tr[3] = m.group(2);
				if(tr[1].contains("<br />")){
					String trr[] = tr[1].split("<br />");
					tr[1]=trr[0];
					tr[2]=trr[1];
					trr = tr[1].split(" \\| ");
					for (String string : trr) {
						String trrr[] = new String[4];
						trrr[0]= tr[0];
						trrr[1]= string;
						trrr[2]= tr[2];
						trrr[3]= tr[3];
						trs.add(trrr);
						abc.add(trrr[1]);
					}
				}else{
					String trr[] = tr[1].split(" / ");
					tr[1]=trr[0];
					tr[2]=trr[1];
					trs.add(tr);
					abc.add(tr[1]);
				}
				
				
			}
			//			count++;
		}

		br.close();

		Alphabet<String> alphabet = Alphabets.fromCollection(abc);
		CompactMealy<String, Word<String>> mealym = new CompactMealy<String, Word<String>>(alphabet);

		Map<String,Integer> states = new HashMap<String,Integer>();
		Integer si=null,sf=null;

		Map<String,Word<String>> words = new HashMap<String,Word<String>>();		


		WordBuilder<String> aux = new WordBuilder<>();

		aux.clear();
		aux.append(OMEGA_SYMBOL);
		words.put(OMEGA_SYMBOL.toString(), aux.toWord());


		for (String[] tr : trs) {
			if(!states.containsKey(tr[0])) states.put(tr[0], mealym.addState());
			if(!states.containsKey(tr[3])) states.put(tr[3], mealym.addState());

			si = states.get(tr[0]);
			sf = states.get(tr[3]);

			if(!words.containsKey(tr[1])){
				aux.clear();
				aux.add(tr[1]);
				words.put(tr[1], aux.toWord());
			}
			if(!words.containsKey(tr[2])){
				aux.clear();
				aux.add(tr[2]);
				words.put(tr[2], aux.toWord());
			}
			mealym.addTransition(si, words.get(tr[1]).toString(), sf, words.get(tr[2]));
		}

		for (Integer st : mealym.getStates()) {
			for (String in : alphabet) {
				//				System.out.println(mealym.getTransition(st, in));
				if(mealym.getTransition(st, in)==null){
					mealym.addTransition(st, in, st, OMEGA_SYMBOL);
				}
			}
		}


		mealym.setInitialState(states.get("s0"));

		return mealym;
	}


	public static void removeSelfLoops(CompactMealy<String, Word<String>> mealy){
		for (Integer st : mealy.getStates()) {
			for (String in : mealy.getInputAlphabet()) {
				if(mealy.getTransition(st, in).getSuccId() == st){
					mealy.removeAllTransitions(st, in);
				}
			}
		}
	}

	public static void generateTabularLog(File filelog){
		try {
			File out_txt = new File(filelog.getParentFile().getParentFile().getName()+"_"+filelog.getName().replaceAll(".xml.log$","_output.txt"));			
			PrintStream out = new PrintStream(new FileOutputStream(out_txt));
			System.setOut(out);

			System.out.print("scenario");
			System.out.print("\t");
			System.out.print("config");
			System.out.print("\t");
			System.out.print("learning");
			System.out.print("\t");
			System.out.print("search_ce");
			System.out.print("\t");
			System.out.print("mq_resets");
			System.out.print("\t");
			System.out.print("mq_symbol");
			System.out.print("\t");
			System.out.print("eq_resets");
			System.out.print("\t");
			System.out.print("eq_symbol");
			
			System.out.println();

			Map<String,Integer> noError = new HashMap<>();


			BufferedReader br = new BufferedReader(new FileReader(filelog));

			String line;
			Pattern numberEof = Pattern.compile("INFO: [^:]+: ([a-zA-Z_0-9.]+)");
			Matcher noEof;

			StringBuffer sb = new StringBuffer();
			StringBuffer fname = new StringBuffer();				
			int noReads = 0;
			while (br.ready()) {
				line = br.readLine();
				noEof = numberEof.matcher(line);
				noEof.matches();
				if(line.startsWith("INFO: Scenario name:")){
					sb.append((noEof.group(1)));
					noReads++;

					fname.delete(0, fname.length());
					fname.append((noEof.group(1)));
				}else  if(line.startsWith("INFO: Configuration:")){
					sb.append("\t");
					sb.append((noEof.group(1)));
					noReads++;

					fname.append("\t");
					fname.append((noEof.group(1)));
					//					}else  if(line.startsWith("INFO: Step:")){
					//						sb.append("\t");
					//						sb.append((noEof.group(1)));
					//						noReads++;
					//
					//						fname.append("\t");
					//						fname.append((noEof.group(1)));
					//						noError.putIfAbsent(fname.toString(), 0);
				}else  if(line.startsWith("INFO: Learning [ms]:")){
					sb.append("\t");
					sb.append((noEof.group(1)));
					noReads++;
				}else  if(line.startsWith("INFO: Searching for counterexample [ms]:")){
					sb.append("\t");
					sb.append((noEof.group(1)));
					noReads++;
				}else if(line.startsWith("INFO: membership queries [resets]:")){
					sb.append("\t");
					sb.append((noEof.group(1)));
					noReads++;
				}else  if(line.startsWith("INFO: membership queries [symbols]:")){
					sb.append("\t");
					sb.append((noEof.group(1)));
					noReads++;
				}else  if(line.startsWith("INFO: equivalence queries [resets]:")){
					sb.append("\t");
					sb.append((noEof.group(1)));
					noReads++;
				}else  if(line.startsWith("INFO: equivalence queries [symbols]:")){
					sb.append("\t");
					sb.append((noEof.group(1)));
					noReads++;
				}else  if(line.startsWith("INFO: ERROR:")){
					noError.put(fname.toString(), noError.getOrDefault(fname.toString(),0)+1);
				}

				if(noReads == 8){
					System.out.print(sb.toString());
					System.out.println();
					sb.delete(0, sb.length());
					noReads = 0;
				}

			}

			br.close();
			out.close();

			File noErrors_txt = new File(filelog.getParentFile().getParentFile().getName()+"_"+filelog.getName().replaceAll(".xml.log$","_noErrors.txt"));
			out = new PrintStream(new FileOutputStream(noErrors_txt));
			System.setOut(out);
			System.out.print("scenario");
			System.out.print("\t");
			System.out.print("config");
			System.out.print("\t");
			System.out.print("totErrors");
			System.out.println();

			for (String key : noError.keySet()) {
				System.out.print(key);
				System.out.print("\t");
				System.out.print(noError.get(key));
				System.out.println();
			}
			out.close();


		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
