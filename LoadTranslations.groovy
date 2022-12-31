// :name=LoadTranslations :description=Loads translated segments from directory in project path

import groovy.json.*
import org.omegat.core.Core;
import org.omegat.util.StringUtil;
import org.omegat.core.data.PrepareTMXEntry;
import groovy.io.FileType
import groovy.json.JsonSlurper

files = project.getProjectFiles();

def prop = project.projectProperties
if (!prop)
	return

def root = prop.projectRoot;

def dir = new File(root + "segments_dump_translated")
if (!dir.exists())
	return

def translations = [:]
def jsonSlurper = new JsonSlurper()
dir.eachFile (FileType.FILES) { file ->
	console.println "Loading entries from " + file + "..."
	data = jsonSlurper.parse(file)
	console.println "... loaded " + data.size + " entries"
//  data = JSON.parse(file.text)
//  console.println file
//  console.println data.size
  data.each { val -> 
  	if (val.source == val.target)
  		return

//	console.println val.source
//	console.println val.target
	translations[val.source] = val.target
  }
}

console.println "Total translations loaded: " + translations.size()

translationCount = 0;
fileLoop:
for (i in 0 ..< project.getProjectFiles().size())
{
	fi = files[i];
	for (j in 0 ..< fi.entries.size())
	{
		if (java.lang.Thread.interrupted()) {
			break fileLoop;
		}
	
		ste = fi.entries[j];
		source = ste.getSrcText();
		info = project.getTranslationInfo(ste)
		if (info && info.isTranslated())
			continue;

		target = info ? info.translation : "";
		if (target && target != source)
			continue;
//
//		console.println source
//		console.println translations
//		console.println "" + translations.get(source)
		if (!translations.containsKey(source))
			continue;

		translation = translations[source]
		if (!translation)
			continue

    	 	def prepare = new PrepareTMXEntry()
    	 	prepare.translation = translation
    	 	project.setTranslation(ste, prepare, true, null)
    	 	++translationCount;
	}
}

console.println "Total applied translations: " + translationCount

return
