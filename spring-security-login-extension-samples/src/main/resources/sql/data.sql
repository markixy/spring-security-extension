-- 每次都清空数据！！！
delete from sys_user;

-- BCryptPasswordEncoder 123456 -> $2a$10$shcts4vp58TzPiOrRfPiMu9LPjuC50KxdXM6In2TyzHnFT5pdIF7y
insert into sys_user(id, name, nickname, password, phone, email) values
('001', 'admin', '管理员', '$2a$10$shcts4vp58TzPiOrRfPiMu9LPjuC50KxdXM6In2TyzHnFT5pdIF7y', '15012345678', 'admin@mail.com'),
('002', 'zhangsan', '张三', '$2a$10$shcts4vp58TzPiOrRfPiMu9LPjuC50KxdXM6In2TyzHnFT5pdIF7y', '13012345678', 'zhangsan@mail.com');

