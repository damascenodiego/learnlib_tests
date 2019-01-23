package experiments.uk.ac.le;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;
import java.util.Random;

import br.usp.icmc.labes.mealyInference.utils.MyObservationTable;
import br.usp.icmc.labes.mealyInference.utils.OTUtils;
import br.usp.icmc.labes.mealyInference.utils.EquivEQOracle.RandomWMethodHypEQOracle;
import de.learnlib.api.SUL;
import de.learnlib.api.logging.LearnLogger;
import de.learnlib.datastructure.observationtable.ObservationTable;
import de.learnlib.filter.cache.sul.SULCache;
import de.learnlib.filter.statistic.sul.ResetCounterSUL;
import de.learnlib.filter.statistic.sul.SymbolCounterSUL;
import de.learnlib.oracle.membership.SULOracle;
import net.automatalib.words.Word;

public class RunExperimentRandomWMethodQsize extends RunExperimentAbstract {

	public static void main(String[] args) {
		// output directory
		String out_dir_string = "logDir/";
		log_dir = new File(out_dir_string );
		log_dir.mkdirs();

		// random seed
		rnd_seed = new Random(tstamp);
		// timestamp
		timestamp = new Timestamp(tstamp);


		//Set this before the logger start.
		System.setProperty("logdir", out_dir_string+"/RunExperimentRandomWMethodQsize/");
		System.setProperty("logtstamp", sdf.format(timestamp).replaceAll("/", "_"));

		logger = LearnLogger.getLogger(RunExperimentRandomWMethodQsize.class);

		try {
			loadModels();

			if((args.length==1) && args[0].matches("^[0-9]+$")){
				int reps = Integer.valueOf(args[0].replaceAll("^-reps=",""));
				for (int i = 0; i < reps; i++) {
					for (List<MealyPlusFile> list_of_suls : list_of_list_of_suls) {
						for (MealyPlusFile sul_i : list_of_suls) {
							{
								logger.logEvent("Start LStar @"+sul_i.getFile().getName());
								setupSUL(sul_i);
								setupMQOracle();
								setupEQOracle();
								setupInitialSetsDefault(sul_i);
								buildAndRunExperiment(sul_i);
								logger.logEvent("End LStar @"+sul_i.getFile().getName());
							}

							{
								logger.logEvent("Start L1 @"+sul_i.getFile().getName());
								setupSUL(sul_i);
								setupMQOracle();
								setupEQOracle();
								setupInitialSetsL1();
								buildAndRunExperiment(sul_i);
								logger.logEvent("End L1 @"+sul_i.getFile().getName());
							}

							for (MealyPlusFile sul_j : list_of_suls) {
								if(sul_i.equals(sul_j)) continue;
								{
									logger.logEvent("Start AdaptiveLstar @"+sul_i.getFile().getName()+" w/"+sul_j.getFile().getName());
									setupSUL(sul_i);
									setupMQOracle();
									setupEQOracle();
									setupInitialSetsFromOT(sul_i,sul_j);
									initPrefixes.clear(); initPrefixes.add(Word.epsilon());
									buildAndRunExperiment(sul_i);
									logger.logEvent("End AdaptiveLstar @"+sul_i.getFile().getName()+" w/"+sul_j.getFile().getName());
								}

								{
									logger.logEvent("Start DLstar_v1 @"+sul_i.getFile().getName()+" w/"+sul_j.getFile().getName());
									setupSUL(sul_i);
									setupMQOracle();
									setupEQOracle();
									setupInitialSetsFromOT(sul_i,sul_j);
									MyObservationTable myot = new MyObservationTable(initPrefixes,initSuffixes);
									logger.logEvent("Revalidate OT");
									ObservationTable<String, Word<Word<String>>> reval_ot = OTUtils.getInstance().revalidateObservationTable(myot, mqOracle,sul_i.getMealyss());
									// learning statistics
									logger.logEvent("Reused queries [resets]: " +(mq_rst.getStatisticalData()).getCount());
									logger.logEvent("Reused queries [symbols]: "+(mq_sym.getStatisticalData()).getCount());
									initPrefixes.clear(); initPrefixes.addAll(reval_ot.getShortPrefixes());
									initSuffixes.clear(); initSuffixes.addAll(reval_ot.getSuffixes());
									buildAndRunExperiment(sul_i);
									logger.logEvent("End DLstar_v1 @"+sul_i.getFile().getName()+" w/"+sul_j.getFile().getName());
								}

								{
									logger.logEvent("Start DLstar_v2 @"+sul_i.getFile().getName()+" w/"+sul_j.getFile().getName());
									setupSUL(sul_i);
									setupMQOracle();
									setupEQOracle();
									setupInitialSetsFromOT(sul_i,sul_j);
									buildAndRunDynamicExperiment(sul_i);
									logger.logEvent("End DLstar_v2 @"+sul_i.getFile().getName()+" w/"+sul_j.getFile().getName());
								}
							}						
						}
					} // end for-each list_of_list_of_suls
				}
			}else {
				System.err.println("java -cp file.jar uk.ac.le.experiment.RunExperimentRandomWMethodQsize <numberOfRepetitions>");
				System.exit(1);
			}

		}catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	protected static void setupEQOracle() {
		// Counters for EQs
		eq_sym = new SymbolCounterSUL<>("EQ", sulSim);
		eq_rst = new ResetCounterSUL <>("EQ", eq_sym);

		// SUL for counting queries wraps sul
		SUL<String, Word<String>> eq_sul = eq_rst;

		//// SULs for associating the IncrementalMealyBuilder 'cbuilder' to EQs
		//eq_sul = new SULCache<>(cbuilder, eq_rst);
		//SULOracle<String,Word<String>> oracleForEQoracle = new SULOracle<>(eq_sul);
		SULOracle<String,Word<String>> oracleForEQoracle = new SULOracle<>(eq_rst);
		
		// set EQ oracle ...
		long long_seed = rnd_seed.nextLong();
		logger.logEvent("Seed: "+long_seed);
		rnd_seed.setSeed(long_seed);
		eqOracle = new RandomWMethodHypEQOracle<>(oracleForEQoracle, 1, 10, 10000, sulMealy);
		logger.logEvent("EquivalenceOracle: RandomWMethodQsizeEQOracle(1, 10, 10000)");

	}
}