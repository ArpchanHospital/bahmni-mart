SELECT
     CONCAT("\"", questionConcept.concept_full_name, "\"")                                  AS `Fully Specified Name`,
     CONCAT("\"", questionConcept.concept_short_name, "\"")                                 AS `Question`,
     questionConcept.concept_datatype_name                                                  AS `Qeustion Datatype`,
     CONCAT("\"", questionConcept.description, "\"")                                        AS `Description`,
     crtv.code                                                                              AS `Question Header`,
     CONCAT("\"",answerConcept.answer, "\"")                                                AS `Answer`,
     crtvForAnswerCode.code                                                                 AS `Answer Code`
  FROM concept_set cs
    LEFT JOIN (SELECT
                    ca.concept_id,
                    cv.concept_full_name                      AS `answer`,
                    ca.answer_concept
              FROM concept_answer ca
              INNER JOIN concept_view cv ON cv.concept_id = ca.answer_concept) answerConcept ON answerConcept.concept_id = cs.concept_id
    INNER JOIN concept_view questionConcept ON questionConcept.concept_id = cs.concept_id AND questionConcept.concept_short_name IS NOT NULL
    LEFT JOIN concept_reference_term_map_view crtvForAnswerCode ON crtvForAnswerCode.concept_id = answerConcept.answer_concept AND crtvForAnswerCode.concept_reference_source_name = 'MSF-INTERNAL'
    LEFT JOIN concept_reference_term_map_view crtv ON  cs.concept_id = crtv.concept_id AND crtv.concept_map_type_name = 'SAME-AS' AND crtv.concept_reference_source_name = 'MSF-INTERNAL'
UNION
SELECT
      "Drug Code"                                                         AS `Fully Specified Name`,
      "drug_code"                                                         AS `question`,
      "Drug"                                                              AS `question_datatype`,
      concat("\"", drugDesc.description, "\"")                              AS `description`,
      "drug_code"                                                         AS `header`,
      concat("\"", drugCV.name, "\"")                        AS `answer`,
      drugCrtv.code                                                       AS `answer code`
    FROM drug drugCV
    LEFT JOIN concept_description drugDesc on drugDesc.concept_id = drugCV.concept_id
    INNER JOIN concept_reference_term_map_view drugCrtv ON drugCrtv.concept_id = drugCV.concept_id
                                            AND concept_reference_source_name = 'MSF-INTERNAL'
UNION
SELECT CONCAT('\"',"Patient Identifier", '\"'),"", "Text","", CONCAT('\"',"patient_id",'\"'), "",""
UNION
SELECT CONCAT('\"',"Program Identifier", '\"'),"", "Text","", CONCAT('\"',"patient_prg_id",'\"'), "",""
UNION
SELECT CONCAT('\"',"Drug Name", '\"'),"", "Text","", CONCAT('\"',"drug_name",'\"'), "",""
UNION
SELECT CONCAT('\"',"Drug Start Date", '\"'),"", "Date", "", CONCAT('\"',"drug_start",'\"'), "",""
UNION
SELECT CONCAT('\"',"Drug End Date", '\"'),"", "Date", "", CONCAT('\"',"drug_stop",'\"'), "",""
UNION
SELECT CONCAT('\"',"Drug dose", '\"'),"", "Numeric", "", CONCAT('\"',"dose",'\"'),"",""
UNION
SELECT CONCAT('\"',"Additional Instructions", '\"'),"", "Text", "", CONCAT('\"',"additional_instr",'\"'), "",""
UNION
SELECT CONCAT('\"',"Location Name", '\"'),"", "Text", "", CONCAT('\"',"location",'\"'), "",""
UNION
SELECT CONCAT('\"',"Drug Duration", '\"'),"", "Numeric", "", CONCAT('\"',"duration",'\"'), "",""
UNION
SELECT CONCAT('\"',"Direct Observation Therapy", '\"'),"", "Coded", "", CONCAT('\"',"dot",'\"'), "Yes",""
UNION
SELECT CONCAT('\"',"Direct Observation Therapy", '\"'),"", "Coded", "", CONCAT('\"',"dot",'\"'), "No",""
UNION
SELECT CONCAT('\"',"Drug Dispense", '\"'),"", "Coded", "", CONCAT('\"',"dispense",'\"'), "Yes",""
UNION
SELECT CONCAT('\"',"Drug Dispense", '\"'),"", "Coded", "", CONCAT('\"',"dispense",'\"'), "No",""
UNION
SELECT CONCAT('\"',"Drug Stop Reason", '\"'),"", "Text", "", CONCAT('\"',"stop_reason",'\"'), "Refused To Take",""
UNION
SELECT CONCAT('\"',"Drug Stop Notes", '\"'),"", "Text", "", CONCAT('\"',"stop_notes",'\"'), "",""
UNION
SELECT CONCAT('\"',"Date Of Birth", '\"'),"", "Date", "", CONCAT('\"',"dob",'\"'), "",""
UNION
SELECT CONCAT('\"',"Patient Age", '\"'),"", "Number", "", CONCAT('\"',"age",'\"'), "",""
UNION
SELECT CONCAT('\"',"Sex", '\"'),"", "Coded", "", CONCAT('\"',"sex",'\"'),"Male","1"
UNION
SELECT CONCAT('\"',"Sex", '\"'),"", "Coded", "", CONCAT('\"',"sex",'\"'),"Female","2"
UNION
SELECT CONCAT('\"',"Program Name", '\"'),"", "Text", "", CONCAT('\"',"prg_name",'\"'),"",""
UNION
SELECT CONCAT('\"',"Program Start Date", '\"'),"", "Date", "", CONCAT('\"',"prg_start_dt",'\"'),"",""
UNION
SELECT CONCAT('\"',"Program End Date", '\"'),"", "Date", "", CONCAT('\"',"prg_end_dt",'\"'),"",""
UNION
SELECT CONCAT('\"',"Program Outcome", '\"'),"", "Misc", "", CONCAT('\"',"prg_outcome",'\"'),"MBA",""
UNION
SELECT CONCAT('\"',"Program Outcome", '\"'),"", "Misc", "", CONCAT('\"',"prg_outcome",'\"'),"Dismissal",""
UNION
SELECT CONCAT('\"',"Program Outcome", '\"'),"", "Misc", "", CONCAT('\"',"prg_outcome",'\"'),"Defaulter (D2)",""
UNION
SELECT CONCAT('\"',"Program Outcome", '\"'),"", "Misc", "", CONCAT('\"',"prg_outcome",'\"'),"Death",""
UNION
SELECT CONCAT('\"',"Program Outcome", '\"'),"", "Misc", "", CONCAT('\"',"prg_outcome",'\"'),"Transfer to Other Structure",""
UNION
SELECT CONCAT('\"',"Program Outcome", '\"'),"", "Misc", "", CONCAT('\"',"prg_outcome",'\"'),"Refused",""
UNION
SELECT CONCAT('\"',"Program State", '\"'),"", "Misc", "", CONCAT('\"',"Prg_state",'\"'),"Identification",""
UNION
SELECT CONCAT('\"',"Program State", '\"'),"", "Misc", "", CONCAT('\"',"Prg_state",'\"'),"Network Follow-up",""
UNION
SELECT CONCAT('\"',"Program State", '\"'),"", "Misc", "", CONCAT('\"',"Prg_state",'\"'),"Pre-Operative",""
UNION
SELECT CONCAT('\"',"Program State", '\"'),"", "Misc", "", CONCAT('\"',"Prg_state",'\"'),"Surgical / Hospitalisation",""
UNION
SELECT CONCAT('\"',"Program State", '\"'),"", "Misc", "", CONCAT('\"',"Prg_state",'\"'),"Rehabilitation",""
UNION
SELECT CONCAT('\"',"Program State Start Date", '\"'),"", "Date", "", CONCAT('\"',"prg_state_start",'\"'),"",""
UNION
SELECT CONCAT('\"',"Program State End Date", '\"'),"", "Date", "", CONCAT('\"',"prg_state_end",'\"'),"",""
UNION
SELECT CONCAT('\"',"Date of surgery", '\"'),"", "Date", "", CONCAT('\"',"date_of_surgery",'\"'),"",""
UNION
SELECT CONCAT('\"',"Start time", '\"'),"", "Date", "", CONCAT('\"',"surgery_start_time",'\"'),"",""
UNION
SELECT CONCAT('\"',"Est time", '\"'),"", "Date", "", CONCAT('\"',"surgery_est_time",'\"'),"",""
UNION
SELECT CONCAT('\"',"Actual time", '\"'),"", "Date", "", CONCAT('\"',"surgery_actual_time",'\"'),"",""
UNION
SELECT CONCAT('\"',"OT", '\"'),"", "Coded", "", CONCAT('\"',"ot",'\"'),"OT 1",""
UNION
SELECT CONCAT('\"',"OT", '\"'),"", "Coded", "", CONCAT('\"',"ot",'\"'),"OT 2",""
UNION
SELECT CONCAT('\"',"OT", '\"'),"", "Coded", "", CONCAT('\"',"ot",'\"'),"OT 3",""
UNION
SELECT CONCAT('\"',"Procedures", '\"'),"", "Text", "", CONCAT('\"',"procedures",'\"'),"",""
UNION
SELECT CONCAT('\"',"Notes", '\"'),"", "Text", "", CONCAT('\"',"surgery_notes",'\"'),"",""
UNION
SELECT CONCAT('\"',"Other Surgeon", '\"'),"", "Text", "", CONCAT('\"',"other_surgeon",'\"'),"",""
UNION
SELECT CONCAT('\"',"Surgical Assistant", '\"'),"", "Text", "", CONCAT('\"',"surgical_assistant",'\"'),"",""
UNION
SELECT CONCAT('\"',"Anaesthetist", '\"'),"", "Text", "", CONCAT('\"',"anaesthetist",'\"'),"",""
UNION
SELECT CONCAT('\"',"Scrub Nurse", '\"'),"", "Text", "", CONCAT('\"',"scrub_nurse",'\"'),"",""
UNION
SELECT CONCAT('\"',"Circulating Nurse", '\"'),"", "Text", "", CONCAT('\"',"circulating_nurse",'\"'),"",""
UNION
SELECT CONCAT('\"',"Status", '\"'),"", "Text", "", CONCAT('\"',"surgery_status",'\"'),"",""
UNION
SELECT CONCAT('\"',"Status change notes", '\"'),"", "Text", "", CONCAT('\"',"status_change_notes",'\"'),"",""
UNION
SELECT CONCAT('\"',"Surgeon", '\"'),"", "Coded", "", CONCAT('\"',"surgeon",'\"'),CONCAT(pn.given_name, ' ', pn.family_name),""
FROM provider p
INNER JOIN person_name pn ON p.person_id = pn.person_id
UNION
SELECT CONCAT('\"',"Location", '\"'),"", "Coded", "", CONCAT('\"',"location",'\"'),"Ward",""
UNION
SELECT CONCAT('\"',"Location", '\"'),"", "Coded", "", CONCAT('\"',"location",'\"'),"Ward (2nd floor)",""
UNION
SELECT CONCAT('\"',"Location", '\"'),"", "Coded", "", CONCAT('\"',"location",'\"'),"Ward (3rd floor)",""
UNION
SELECT CONCAT('\"',"Location", '\"'),"", "Coded", "", CONCAT('\"',"location",'\"'),"Rehabilitation Center",""
UNION
SELECT CONCAT('\"',"Location", '\"'),"", "Coded", "", CONCAT('\"',"location",'\"'),"Rehabilitation Center (4th floor)",""
UNION
SELECT CONCAT('\"',"Location", '\"'),"", "Coded", "", CONCAT('\"',"location",'\"'),"Rehabilitation Center (5th floor)",""
UNION
SELECT CONCAT('\"',"Location", '\"'),"", "Coded", "", CONCAT('\"',"location",'\"'),"Kahramana",""
UNION
SELECT CONCAT('\"',"Location", '\"'),"", "Coded", "", CONCAT('\"',"location",'\"'),"Kahramana(1st floor)",""
UNION
SELECT CONCAT('\"',"Location", '\"'),"", "Coded", "", CONCAT('\"',"location",'\"'),"Kahramana(2nd floor)",""
UNION
SELECT CONCAT('\"',"Location", '\"'),"", "Coded", "", CONCAT('\"',"location",'\"'),"Buffer Beds",""
UNION
SELECT CONCAT('\"',"Bed Id", '\"'),"", "Text", "", CONCAT('\"',"bed_id",'\"'),"",""
UNION
SELECT CONCAT('\"',"Action", '\"'),"", "Coded", "", CONCAT('\"',"action",'\"'),"MOVEMENT",""
UNION
SELECT CONCAT('\"',"Action", '\"'),"", "Coded", "", CONCAT('\"',"action",'\"'),"DISCHARGE",""
UNION
SELECT CONCAT('\"',"Action", '\"'),"", "Coded", "", CONCAT('\"',"action",'\"'),"ADMISSION",""
UNION
SELECT CONCAT('\"',"Admission date", '\"'),"", "Date", "", CONCAT('\"',"admission_date",'\"'),"",""
UNION
SELECT CONCAT('\"',"Discharge date", '\"'),"", "Date", "", CONCAT('\"',"discharge_date",'\"'),"",""
UNION
SELECT CONCAT('\"',"Bed tag", '\"'),"", "Coded", "", CONCAT('\"',"bed_tag",'\"'),"Lost",""
UNION
SELECT CONCAT('\"',"Bed tag", '\"'),"", "Coded", "", CONCAT('\"',"bed_tag",'\"'),"Isolation",""
UNION
SELECT CONCAT('\"',"Bed tag", '\"'),"", "Coded", "", CONCAT('\"',"bed_tag",'\"'),"Strict Isolation",""
UNION
SELECT CONCAT('\"',"Bed tag", '\"'),"", "Coded", "", CONCAT('\"',"bed_tag",'\"'),"Reserved for CT",""
UNION
SELECT CONCAT('\"',"Bed tag start time", '\"'),"", "Date", "", CONCAT('\"',"bed_tag_start",'\"'),"",""
UNION
SELECT CONCAT('\"',"Bed tag end time", '\"'),"", "Date", "", CONCAT('\"',"bed_tag_end",'\"'),"","";