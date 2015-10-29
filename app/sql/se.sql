-- phpMyAdmin SQL Dump
-- version 4.0.10.10
-- http://www.phpmyadmin.net
--
-- Host: 127.4.93.130:3306
-- Generation Time: Oct 13, 2015 at 08:08 AM
-- Server version: 5.5.45
-- PHP Version: 5.3.3

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `se`
--

-- --------------------------------------------------------

--
-- Table structure for table `admin`
--

CREATE TABLE IF NOT EXISTS `admin` (
  `username` varchar(128) COLLATE utf8_bin NOT NULL,
  `password` varchar(128) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `admin`
--

INSERT INTO `admin` (`username`, `password`) VALUES
('admin', '123');

-- --------------------------------------------------------

--
-- Table structure for table `app`
--

CREATE TABLE IF NOT EXISTS `app` (
  `appid` int(11) NOT NULL,
  `appname` varchar(128) COLLATE utf8_bin NOT NULL,
  `appcategory` varchar(20) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`appid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `appusage`
--

CREATE TABLE IF NOT EXISTS `appusage` (
  `timestamp` datetime NOT NULL,
  `macaddress` varchar(40) COLLATE utf8_bin NOT NULL,
  `appid` int(8) NOT NULL,
  PRIMARY KEY (`timestamp`,`macaddress`),
  KEY `appUsageID_fk1` (`macaddress`),
  KEY `appUsageID_fk2` (`appid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `location`
--

CREATE TABLE IF NOT EXISTS `location` (
  `locationid` int(40) NOT NULL,
  `semanticplace` varchar(128) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`locationid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `locationusage`
--

CREATE TABLE IF NOT EXISTS `locationusage` (
  `timestamp` datetime NOT NULL,
  `macaddress` varchar(40) COLLATE utf8_bin DEFAULT NULL,
  `locationid` int(40) NOT NULL,
  PRIMARY KEY (`timestamp`,`macaddress`),
  KEY `locationUsage_fk2` (`locationid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE IF NOT EXISTS `user` (
  `macaddress` varchar(40) COLLATE utf8_bin NOT NULL,
  `name` varchar(128) COLLATE utf8_bin NOT NULL,
  `password` varchar(128) COLLATE utf8_bin NOT NULL,
  `email` varchar(128) COLLATE utf8_bin NOT NULL,
  `gender` char(1) COLLATE utf8_bin NOT NULL,
  `cca` varchar(63) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`macaddress`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `user`
--

--
-- Constraints for dumped tables
--

--
-- Constraints for table `appusage`
--
ALTER TABLE `appusage`
  ADD CONSTRAINT `appUsageID_fk1` FOREIGN KEY (`macaddress`) REFERENCES `user` (`macaddress`),
  ADD CONSTRAINT `appUsageID_fk2` FOREIGN KEY (`appid`) REFERENCES `app` (`appid`);

--
-- Constraints for table `locationusage`
--
ALTER TABLE `locationusage`
  ADD CONSTRAINT `locationUsage_fk2` FOREIGN KEY (`locationid`) REFERENCES `location` (`locationid`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
