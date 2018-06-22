SELECT
  pi.*,
  pd.gender,
  pd.birthdate               AS birth_date,
  pd.dead,
  pa.*,
  pap.appointment_id,
  pap.appointment_provider,
  pap.appointment_start_time,
  pap.appointment_end_time,
  pap.appointment_speciality,
  pap.appointment_service,
  pap.appointment_service_duration,
  pap.appointment_service_type,
  pap.appointment_service_type_duration,
  pap.appointment_status,
  pap.appointment_location,
  pap.appointment_kind
FROM person_details_default pd
  LEFT JOIN person_attributes pa ON pa.person_id = pd.person_id
  LEFT JOIN patient_identifier pi ON pi.patient_id = pd.person_id
  LEFT JOIN patient_appointment_default pap ON pap.patient_id = pa.person_id