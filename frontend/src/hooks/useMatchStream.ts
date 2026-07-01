import { useEffect, useRef, useState } from 'react';
import type { Match } from '../types';
import { api, getStreamUrl } from '../api/client';

const POLL_INTERVAL = 15000;

export function useMatchStream(matchId: string | undefined, initial: Match | null) {
  const [match, setMatch] = useState<Match | null>(initial);
  const pollRef = useRef<ReturnType<typeof setInterval> | null>(null);

  useEffect(() => {
    setMatch(initial);
  }, [initial]);

  useEffect(() => {
    if (!matchId || !initial) return;
    if (initial.finished) return;

    let eventSource: EventSource | null = null;

    const fetchUpdate = async () => {
      try {
        const updated = await api.getMatch(matchId);
        setMatch(updated);
        return updated;
      } catch {
        return null;
      }
    };

    const startPolling = () => {
      if (pollRef.current) return;
      pollRef.current = setInterval(async () => {
        const updated = await fetchUpdate();
        if (updated?.finished && pollRef.current) {
          clearInterval(pollRef.current);
          pollRef.current = null;
        }
      }, POLL_INTERVAL);
    };

    const stopPolling = () => {
      if (pollRef.current) {
        clearInterval(pollRef.current);
        pollRef.current = null;
      }
    };

    // Toujours actif tant que le match n'est pas terminé (à venir ou en cours)
    startPolling();

    try {
      eventSource = new EventSource(getStreamUrl(matchId));

      eventSource.addEventListener('scoreEvent', (e) => {
        try {
          const data = JSON.parse(e.data) as Match;
          setMatch(data);
          if (data.finished) {
            eventSource?.close();
            stopPolling();
          }
        } catch {
          /* ignore */
        }
      });

      eventSource.onerror = () => {
        eventSource?.close();
        eventSource = null;
      };
    } catch {
      /* polling already started as fallback */
    }

    return () => {
      eventSource?.close();
      stopPolling();
    };
  }, [matchId, initial?.id, initial?.finished]);

  return match;
}
