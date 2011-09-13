
-module(srvbridge_handler).

%-behaviour(gen_server).

%% API
-export([start_link/0]).

%% Supervisor callbacks
-export([]).


%% ===================================================================
%% API functions
%% ===================================================================

start_link() ->
    gen_server:start_link({local, ?MODULE}, ?MODULE, []).

%% ===================================================================
%% gen_server callbacks
%% ===================================================================


