package documents.services.rezscore;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.ToString;

@ToString
@XmlRootElement(name = "extended", namespace = "")
@XmlAccessorType(XmlAccessType.NONE)
public class Extended {

	@XmlElement(name = "unix_fail")
	private String unix_fail;

	@XmlElement(name = "kincaid_score")
	private Long kincaid_score;

	@XmlElement(name = "ari_score")
	private Long ari_score;

	@XmlElement(name = "colman_liau_score")
	private Long colman_liau_score;

	@XmlElement(name = "flesch_index")
	private String flesch_index;

	@XmlElement(name = "fog_index")
	private Long fog_index;

	@XmlElement(name = "lix_score")
	private Long lix_score;

	@XmlElement(name = "lix_school_year")
	private Long lix_school_year;

	@XmlElement(name = "smog_grading")
	private Long smog_grading;

	@XmlElement(name = "passive_sentence_pct")
	private Long passive_sentence_pct;

	@XmlElement(name = "avg_syllables")
	private Long avg_syllables;

	@XmlElement(name = "sentence_length")
	private Long sentence_length;

	@XmlElement(name = "short_sentence_pct")
	private Long short_sentence_pct;

	@XmlElement(name = "short_sentence_max")
	private Long short_sentence_max;

	@XmlElement(name = "long_sentence_pct")
	private Long long_sentence_pct;

	@XmlElement(name = "long_sentence_min")
	private Long long_sentence_min;

	@XmlElement(name = "wordcount")
	private Long wordcount;

	@XmlElement(name = "avg_syllable_dev")
	private Long avg_syllable_dev;

	@XmlElement(name = "is_resume_pct")
	private Boolean is_resume_pct;

	@XmlElement(name = "has_linkedin")
	private Boolean has_linkedin;

	@XmlElement(name = "has_objective_section")
	private Boolean has_objective_section;

	@XmlElement(name = "has_references")
	private Boolean has_references;

	@XmlElement(name = "years_experience")
	private Long years_experience;

	@XmlElement(name = "is_working")
	private Long is_working;

	@XmlElement(name = "pct_buzzwords")
	private Long pct_buzzwords;

	@XmlElement(name = "pct_numbers")
	private Long pct_numbers;

	@XmlElement(name = "pct_whitespace")
	private Long pct_whitespace;

	@XmlElement(name = "pct_adverbs")
	private Long pct_adverbs;

	@XmlElement(name = "count_raw")
	private Long count_raw;

	@XmlElement(name = "count_nf")
	private Long count_nf;

	public Extended() {
	}

	public String getUnix_fail() {
		return unix_fail;
	}

	public void setUnix_fail(String unix_fail) {
		this.unix_fail = unix_fail;
	}

	public Long getKincaid_score() {
		return kincaid_score;
	}

	public void setKincaid_score(Long kincaid_score) {
		this.kincaid_score = kincaid_score;
	}

	public Long getAri_score() {
		return ari_score;
	}

	public void setAri_score(Long ari_score) {
		this.ari_score = ari_score;
	}

	public Long getColman_liau_score() {
		return colman_liau_score;
	}

	public void setColman_liau_score(Long colman_liau_score) {
		this.colman_liau_score = colman_liau_score;
	}

	public String getFlesch_index() {
		return flesch_index;
	}

	public void setFlesch_index(String flesch_index) {
		this.flesch_index = flesch_index;
	}

	public Long getFog_index() {
		return fog_index;
	}

	public void setFog_index(Long fog_index) {
		this.fog_index = fog_index;
	}

	public Long getLix_score() {
		return lix_score;
	}

	public void setLix_score(Long lix_score) {
		this.lix_score = lix_score;
	}

	public Long getLix_school_year() {
		return lix_school_year;
	}

	public void setLix_school_year(Long lix_school_year) {
		this.lix_school_year = lix_school_year;
	}

	public Long getSmog_grading() {
		return smog_grading;
	}

	public void setSmog_grading(Long smog_grading) {
		this.smog_grading = smog_grading;
	}

	public Long getPassive_sentence_pct() {
		return passive_sentence_pct;
	}

	public void setPassive_sentence_pct(Long passive_sentence_pct) {
		this.passive_sentence_pct = passive_sentence_pct;
	}

	public Long getAvg_syllables() {
		return avg_syllables;
	}

	public void setAvg_syllables(Long avg_syllables) {
		this.avg_syllables = avg_syllables;
	}

	public Long getSentence_length() {
		return sentence_length;
	}

	public void setSentence_length(Long sentence_length) {
		this.sentence_length = sentence_length;
	}

	public Long getShort_sentence_pct() {
		return short_sentence_pct;
	}

	public void setShort_sentence_pct(Long short_sentence_pct) {
		this.short_sentence_pct = short_sentence_pct;
	}

	public Long getShort_sentence_max() {
		return short_sentence_max;
	}

	public void setShort_sentence_max(Long short_sentence_max) {
		this.short_sentence_max = short_sentence_max;
	}

	public Long getLong_sentence_pct() {
		return long_sentence_pct;
	}

	public void setLong_sentence_pct(Long long_sentence_pct) {
		this.long_sentence_pct = long_sentence_pct;
	}

	public Long getLong_sentence_min() {
		return long_sentence_min;
	}

	public void setLong_sentence_min(Long long_sentence_min) {
		this.long_sentence_min = long_sentence_min;
	}

	public Long getWordcount() {
		return wordcount;
	}

	public void setWordcount(Long wordcount) {
		this.wordcount = wordcount;
	}

	public Long getAvg_syllable_dev() {
		return avg_syllable_dev;
	}

	public void setAvg_syllable_dev(Long avg_syllable_dev) {
		this.avg_syllable_dev = avg_syllable_dev;
	}

	public Boolean getIs_resume_pct() {
		return is_resume_pct;
	}

	public void setIs_resume_pct(Boolean is_resume_pct) {
		this.is_resume_pct = is_resume_pct;
	}

	public Boolean getHas_linkedin() {
		return has_linkedin;
	}

	public void setHas_linkedin(Boolean has_linkedin) {
		this.has_linkedin = has_linkedin;
	}

	public Boolean getHas_objective_section() {
		return has_objective_section;
	}

	public void setHas_objective_section(Boolean has_objective_section) {
		this.has_objective_section = has_objective_section;
	}

	public Boolean getHas_references() {
		return has_references;
	}

	public void setHas_references(Boolean has_references) {
		this.has_references = has_references;
	}

	public Long getYears_experience() {
		return years_experience;
	}

	public void setYears_experience(Long years_experience) {
		this.years_experience = years_experience;
	}

	public Long getIs_working() {
		return is_working;
	}

	public void setIs_working(Long is_working) {
		this.is_working = is_working;
	}

	public Long getPct_buzzwords() {
		return pct_buzzwords;
	}

	public void setPct_buzzwords(Long pct_buzzwords) {
		this.pct_buzzwords = pct_buzzwords;
	}

	public Long getPct_numbers() {
		return pct_numbers;
	}

	public void setPct_numbers(Long pct_numbers) {
		this.pct_numbers = pct_numbers;
	}

	public Long getPct_whitespace() {
		return pct_whitespace;
	}

	public void setPct_whitespace(Long pct_whitespace) {
		this.pct_whitespace = pct_whitespace;
	}

	public Long getPct_adverbs() {
		return pct_adverbs;
	}

	public void setPct_adverbs(Long pct_adverbs) {
		this.pct_adverbs = pct_adverbs;
	}

	public Long getCount_raw() {
		return count_raw;
	}

	public void setCount_raw(Long count_raw) {
		this.count_raw = count_raw;
	}

	public Long getCount_nf() {
		return count_nf;
	}

	public void setCount_nf(Long count_nf) {
		this.count_nf = count_nf;
	}

}
