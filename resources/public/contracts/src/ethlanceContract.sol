pragma solidity ^0.4.4;

import "ethlanceSetter.sol";
import "contractLibrary.sol";

contract EthlanceContract is EthlanceSetter {

    function EthlanceContract(address _ethlanceDB) {
        if(_ethlanceDB == 0x0) throw;
        ethlanceDB = _ethlanceDB;
    }

    function addJobContract(
        uint contractId,
        string description,
        bool isHiringDone
    )
        onlyActiveSmartContract
        onlyActiveEmployer
    {
        if (bytes(description).length > getConfig("max-contract-desc")) throw;
        ContractLibrary.addContract(ethlanceDB, getSenderUserId(), contractId, description, isHiringDone);
    }

    function addJobContractFeedback(
        uint contractId,
        string feedback,
        uint8 rating
    )
        onlyActiveSmartContract
        onlyActiveUser
    {
        if (bytes(feedback).length > getConfig("max-feedback")) throw;
        if (bytes(feedback).length < getConfig("min-feedback")) throw;
        if (rating > 100) throw;
        ContractLibrary.addFeedback(ethlanceDB, contractId, getSenderUserId(), feedback, rating);
    }

    function addJobProposal(
        uint jobId,
        string description,
        uint rate
    )
        onlyActiveSmartContract
        onlyActiveFreelancer
    {
        if (bytes(description).length > getConfig("max-proposal-desc")) throw;
        ContractLibrary.addProposal(ethlanceDB, jobId, getSenderUserId(), description, rate);
    }

    function addJobInvitation(
        uint jobId,
        uint freelancerId,
        string description
    )
        onlyActiveSmartContract
        onlyActiveEmployer
    {
        if (bytes(description).length > getConfig("max-invitation-desc")) throw;
        ContractLibrary.addInvitation(ethlanceDB, getSenderUserId(), jobId, freelancerId, description);
    }
}