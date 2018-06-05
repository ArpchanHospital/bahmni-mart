SELECT
  pat.*,
  pa.address1,
  pa.address2,
  pa.address3,
  pa.address4,
  pa.address6,
  pa.state_province,
  pd.gender,
  pd.birthdate,
  pd.birthtime,
  pd.birthdate_estimated,
  ppd.program_id,
  pg.program_name,
  ppd.patient_program_id,
  ppd.program_outcome,
  ps.state,
  ps.state_name,
  ppd.date_enrolled,
  ppd.date_completed,
  ppd.location_id,
  ppd.location_name,
  ppd.creator_id,
  ppd.creator_name,
  ppd.date_created,
  ppd.date_changed,
  ppd.changed_by_id,
  ppd.changed_by_name

FROM person_details pd LEFT OUTER JOIN person_address pa ON pa.person_id = pd.person_id
  LEFT OUTER JOIN person_attributes pat ON pat.person_id = pa.person_id
  LEFT OUTER JOIN patient_program_data ppd ON ppd.patient_id = pat.person_id
  LEFT OUTER JOIN programs pg ON pg.program_id = ppd.program_id
  LEFT OUTER JOIN patient_state ps ON ps.patient_program_id = ppd.patient_program_id