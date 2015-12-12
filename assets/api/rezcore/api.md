
# GET http://rezscore.com/a/75ccf8/grade?resume={resume}
Where resume is a plain text document like the following:

```
Carl S. Cooper
123 Main Street, San Francisco, CA 94122
Home : 000-000-0000 Cell: 000-000-0000
email@example.com
Professional Summary

Astronomer with government and academic experience conducting astrological research, writing research papers for publication and making presentations for successful fund raising.Driven by an unquenchable curiosity about the universe and a need to unravel its mysteries.
Core Qualifications

    Complex problem solving
    Advanced mathematical and science skills
    Teamwork
    Science-related software knowledge
    Strong physics background
    Development of techniques to collect and study astronomical data

Experience
January 2007 to gust 2014 National Aeronautics and Space Administration, Cleveland OH Astronomer

    Analyzed large data sets collected through observatories and satellites.
    Prepared reports on research findings.
    Gathered data to aid in navigation, satellite technology and space exploration.
    Through applied research, developed computer software to assist in accurate observations and data collection.

March 2002 to December 2007 Columbia University Dept. of Applied Physics and Applied Mathematics, New York NY Astronomer

    Worked on research project funded by grants.
    Wrote grant proposals.
    Traveled to remote locations and often worked at night for better observation conditions.
    Assisted in the development of computer software to analyze data.
    Analyzed astronomical and physical data through complex mathematical calculations.

July 1998 to February 2002 The Goddard Institute for Space Studies, New York NY Post-doctoral Research, Astronomy

    Worked with ground-based telescopes to observe the movements of stars, planets and galaxies.
    Authored scientific papers for journals.
    Designed telescopes, lasers and other scientific equipment.
    Conducted applied research related to GPS technology.

Education
1998 California Technical University, Pasadena CA Ph.D in Astronomy

```


Will return the following:

```
<?xml version="1.0" encoding="ISO-8859-1"?>
<rezscore>
	<score>
		<grade>B-</grade>
		<grade_headline>Your Resume is Good</grade_headline>
		<grade_blurb>This resume provides a good picture of your value to an employer, but it can do better.  To create a better resume, keep your resume concise and impactful.  Make sure every word counts, and use vivid language to get employers excited.</grade_blurb>
		<percentile>72</percentile>
		<percentile_suffix>nd</percentile_suffix>
		<normal_img>http://rezscore.com/images/bells/BlueBell99.png</normal_img>
		<brevity_score>64</brevity_score>
		<impact_score>78</impact_score>
		<depth_score>56</depth_score>
		<email>email@example.com</email>
		<phone>-1</phone>
		<job_keywords>data research fund development science technology computer administration writing</job_keywords>
		<rez_id>frqngiBP_K0~</rez_id>
	</score>
	<industry>
		<first_industry_match>Software / Tech</first_industry_match>
		<first_industry_conf>25.9</first_industry_conf>
		<second_industry_match>Science</second_industry_match>
		<second_industry_conf>23.6</second_industry_conf>
		<third_industry_match>Education</third_industry_match>
		<third_industry_conf>22</third_industry_conf>
	</industry>
	<language>
		<word>
			<string>making</string>
			<val>0.625</val>
		</word>
		<word>
			<string>wrote</string>
			<val>0.625</val>
		</word>
		<word>
			<string>conducted</string>
			<val>0.55555555555556</val>
		</word>
		<word>
			<string>large</string>
			<val>0.52380952380952</val>
		</word>
		<word>
			<string>home</string>
			<val>0.47058823529412</val>
		</word>
	</language>
	<advice>
		<tip>
			<short>Your resume is too short</short>
			<long>Resumes under four hundred words work against the candidate about a quarter of the time.  Flesh out your work experience to keep your resume from looking too thin.</long>
		</tip>
	</advice>
	<file>
		<size>1987</size>
		<encoding>ASCII text, with very long lines, with no line terminators</encoding>
		<extension>bin</extension>
		<lang>en</lang>
	</file>
	<extended>
			<unix_fail>0</unix_fail>
			<kincaid_score>12.2</kincaid_score>
			<ari_score>15.4</ari_score>
			<colman_liau_score>
</colman_liau_score>
			<flesch_index>30.9/100</flesch_index>
			<fog_index>16.0</fog_index>
			<lix_score>63.9</lix_score>
			<lix_school_year>12</lix_school_year>
			<smog_grading>13.3</smog_grading>
			<passive_sentence_pct>0</passive_sentence_pct>
			<avg_syllables>1.92</avg_syllables>
			<sentence_length>13.1</sentence_length>
			<short_sentence_pct>47</short_sentence_pct>
			<short_sentence_max>8</short_sentence_max>
			<long_sentence_pct>15</long_sentence_pct>
			<long_sentence_min>23</long_sentence_min>
			<wordcount>253</wordcount>
			<avg_syllable_dev>0.07</avg_syllable_dev>
			<is_resume_pct>0.92370710909091</is_resume_pct>
			<has_linkedin>0</has_linkedin>
			<has_objective_section>0</has_objective_section>
			<has_references>0</has_references>
			<years_experience>-1</years_experience>
			<is_working>-1</is_working>
			<pct_buzzwords>0.044117647058824</pct_buzzwords>
			<pct_numbers>0.0073529411764706</pct_numbers>
			<pct_whitespace>0.069852941176471</pct_whitespace>
			<pct_adverbs>0.0036764705882353</pct_adverbs>
			<count_raw>272</count_raw>
			<count_nf>253</count_nf>
		</extended>
	<text>
		<binlink>
			http://rezscore.com/a/75ccf8/view/frqngiBP_K0~.bin
		</binlink>
		<htmllink>
 
			http://rezscore.com/a/75ccf8/view/frqngiBP_K0~.html
		</htmllink>
		<txtlink>
			http://rezscore.com/a/75ccf8/view/frqngiBP_K0~.txt
		</txtlink>
 
	</text>
</rezscore>
```
