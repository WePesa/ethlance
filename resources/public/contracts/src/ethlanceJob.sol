pragma solidity ^0.4.4;

import "ethlanceSetter.sol";
import "jobLibrary.sol";

contract EthlanceJob is EthlanceSetter {

    function EthlanceJob(address _ethlanceDB) {
        if(_ethlanceDB == 0x0) throw;
        ethlanceDB = _ethlanceDB;
    }

    function addJob(
        string title,
        string description,
        uint[] skills,
        uint language,
        uint budget,
        uint8[] uint8Items
    )
        onlyActiveSmartContract
        onlyActiveEmployer
    {
//        if (bytes(description).length > getConfig("max-job-description")) throw;
//        if (bytes(description).length < getConfig("min-job-description")) throw;
//        if (bytes(title).length > getConfig("max-job-title")) throw;
//        if (bytes(title).length < getConfig("min-job-title")) throw;
//        if (skills.length > getConfig("max-job-skills")) throw;
//        if (skills.length < getConfig("min-job-skills")) throw;
        JobLibrary.addJob(ethlanceDB, getSenderUserId(), title, description, skills, language, budget, uint8Items);
    }

    function setJobHiringDone
    (
        uint jobId
    )
        onlyActiveSmartContract
        onlyActiveEmployer
    {
        JobLibrary.setHiringDone(ethlanceDB, jobId, getSenderUserId());
    }

    function setJobStatus(
        uint jobId,
        uint8 status
    )
        onlyOwner
    {
        JobLibrary.setStatus(ethlanceDB, jobId, status);
    }
}