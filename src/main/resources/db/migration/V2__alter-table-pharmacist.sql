-- Mode 1

-- 0 or 1 - user actived or desactived
-- ALTER TABLE pharmacist ADD active tinyint;
-- update older registers
-- UPDATE pharmacist SET active = 1;



-- Mode 2

-- TRUE or FALSE
ALTER TABLE pharmacist ADD COLUMN active BOOLEAN DEFAULT TRUE;
-- update older registers
UPDATE pharmacist SET active = TRUE WHERE active IS NULL;