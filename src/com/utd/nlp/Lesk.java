package com.utd.nlp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;

public class Lesk {
	private static final String SENT = "Time flies like an arrow";
	private static HashMap<String, ArrayList<String>> sense = new HashMap<>();
	private static HashMap<String, String> alt = new HashMap<>();
	public static void main(String[] args) throws IOException {
		System.setProperty("wordnet.database.dir","WordNet\\2.1\\dict");
		getSenses(SENT.split(" "));
		String[] words = SENT.split(" ");
		for (int i = 0; i < words.length; i++) {
			getBestSense(words[i], sense.get(words[i].toLowerCase()));
		}
	}

	private static void getBestSense(String curr_word, ArrayList<String> senses) {
		int max_count = 0, best_sense_index = 0;
		if(senses!=null){
			for(int i = 0;i < senses.size();i++){
				int count = 0;
				String curr_sent = senses.get(i).replaceAll("[^a-zA-Z ]", "").toLowerCase();
				String[] words = curr_sent.split(" ");
				for(int j = 0;j < words.length;j++){
					for(String word : SENT.split(" ")){
						if(words[j].equalsIgnoreCase(word) || words[j].equalsIgnoreCase(alt.get(word)))
							count++;
					}
				}
				if(count > max_count){
					max_count = count;
					best_sense_index = i;
				}
			}
			System.out.println("Best sense for word " + curr_word + " ==>> " + senses.get(best_sense_index));
		}
	}

		private static void getSenses(String[] words) throws IOException{
			
			WordNetDatabase database = WordNetDatabase.getFileInstance(); 
			Synset synset; 
			String lemma = null;
			StringBuffer signature;
			
			for (String curr_word : words) {
				for(SynsetType synset_type :  SynsetType.ALL_TYPES){
					Synset[] synsets = database.getSynsets(curr_word, synset_type); 
					for (int i = 0; i < synsets.length; i++) {
						ArrayList<String> temp = new ArrayList<>();
						signature = new StringBuffer();
						synset = synsets[i]; 
						String[] temp1 = synset.getWordForms();
						
						for (int j = 0; j < temp1.length; j++) {
							if(temp1[j].equalsIgnoreCase(curr_word))
								lemma = temp1[j];
						}
						if(lemma==null){
							lemma = temp1[0];
							alt.put(curr_word, lemma);
						}
						signature.append(synset.getDefinition() + " ");
						for (int j = 0; j < synset.getUsageExamples().length; j++) {
							signature.append(synset.getUsageExamples()[j] + " ");
						}

						if(sense.containsKey(lemma))
							temp = sense.get(lemma);

						temp.add(signature.toString());
						sense.put(lemma, temp);
					}
				}
			}
		}
	}