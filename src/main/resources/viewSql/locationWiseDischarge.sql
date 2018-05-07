SELECT
  bpam.patient_id,
  bpam.visit_id,
  bpam.location,
  min(bpam.date_started)              AS startDate,
  coalesce(discharge_date(patient_id,bpam.location,date_stopped),current_date) AS dischargeDate
FROM bed_patient_assignment bpam
GROUP BY patient_id,visit_id,dischargeDate,location
ORDER BY startDate,dischargeDate